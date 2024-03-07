package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeleteAccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState.asStateFlow()

    fun setPassword(value: String) = _uiState.update { state -> state.copy(password = value) }

    fun setConfirmDeletion(value: Boolean) =
        _uiState.update { state -> state.copy(confirmingDeletion = value) }

    fun deleteAccount() {
        _uiState.update { s ->
            s.copy(
                passwordError = false,
                loading = true,
                accountDeleted = STATE_LOADING,
                confirmingDeletion = false
            )
        }

        if (uiState.value.password.isEmpty()) {
            _uiState.update { s -> s.copy(loading = false, passwordError = true) }
        } else {
            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(PostalService.retailer.email, uiState.value.password)
                .addOnSuccessListener {
                    val currentUser = Firebase.auth.currentUser
                    if (currentUser != null) {
                        currentUser.delete()
                        _uiState.update { s ->
                            s.copy(
                                loading = false,
                                accountDeleted = STATE_SUCCESS
                            )
                        }
                    } else {
                        _uiState.update { s ->
                            s.copy(
                                loading = false,
                                accountDeleted = STATE_ERROR
                            )
                        }
                        Log.e("FirebaseError", "Current user is null")
                    }
                }.addOnFailureListener { e ->
                    _uiState.update { s -> s.copy(loading = false, passwordError = true) }
                    e.message?.let { message -> Log.e("FirebaseError", message) }
                }
        }
    }

    fun consumeDeletionError() {
        _uiState.update { s -> s.copy(accountDeleted = STATE_LOADING) }
    }
}

data class DeleteAccountUiState(
    val password: String = "",
    val passwordError: Boolean = false,
    val loading: Boolean = false,
    val accountDeleted: Int = STATE_LOADING,
    val confirmingDeletion: Boolean = false
)