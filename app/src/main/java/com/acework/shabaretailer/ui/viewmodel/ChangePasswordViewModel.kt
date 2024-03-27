package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.acework.shabaretailer.PostalService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChangePasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun updateNewPassword(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun updateConfirmPassword(value: String) =
        _uiState.update { it.copy(confirmNewPassword = value) }

    fun validate() {
        _uiState.update {
            it.copy(
                newPasswordError = it.newPassword.length < 8,
                confirmNewPasswordError = it.confirmNewPassword != it.newPassword,
            )
        }

        _uiState.value.let {
            if (!(it.newPasswordError || it.confirmNewPasswordError)) {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        _uiState.update { it.copy(loading = true) }
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(PostalService.retailer.email, uiState.value.currentPassword)
            .addOnSuccessListener {
                val currentUser = Firebase.auth.currentUser
                if (currentUser != null) {
                    currentUser.updatePassword(uiState.value.newPassword).addOnSuccessListener {
                        _uiState.update { s -> s.copy(passwordChanged = true, loading = false) }
                    }.addOnFailureListener { e ->
                        _uiState.update { s ->
                            s.copy(
                                errorChangingPassword = true,
                                loading = false
                            )
                        }
                        e.message?.let { message -> Log.e("FirebaseError", message) }
                    }
                } else {
                    _uiState.update { s -> s.copy(errorChangingPassword = true, loading = false) }
                    Log.e("FirebaseError", "Current user is null")
                }
            }.addOnFailureListener { e ->
                _uiState.update { s -> s.copy(currentPasswordError = true, loading = false) }
                e.message?.let { message -> Log.e("FirebaseError", message) }
            }
    }
}

data class ChangePasswordUiState(
    // input values
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",

    // input errors
    val currentPasswordError: Boolean = false,
    val newPasswordError: Boolean = false,
    val confirmNewPasswordError: Boolean = false,

    // view states
    val loading: Boolean = false,
    val errorChangingPassword: Boolean = false,
    val passwordChanged: Boolean = false
)