package com.acework.shabaretailer.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) = _uiState.update { it.copy(email = value.trim()) }
    fun updatePassword(value: String) = _uiState.update { it.copy(password = value) }
    fun updateLoginError() = _uiState.update { it.copy(loginError = false) }
    fun validate() {
        val emailError = !Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches()
        val passwordError = uiState.value.password.isEmpty()

        var inputHasErrors = false
        if (emailError) inputHasErrors = true
        if (passwordError) inputHasErrors = true

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }

        if (!inputHasErrors) {
            _uiState.update { it.copy(loading = true, loadingMsg = R.string.logging_in) }
            login()
        }
    }

    private fun login() {
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(uiState.value.email, uiState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update { it.copy(loading = false, loginSuccess = true) }
                } else {
                    _uiState.update { it.copy(loading = false, loginError = true) }
                }
            }
    }

    fun setForgotPassword(value: Boolean) {
        _uiState.update { state -> state.copy(forgotPassword = value) }
    }

    fun validateForgotPassword() {
        val emailError = !Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches()
        if (emailError) {
            _uiState.update { state -> state.copy(emailError = true) }
        } else {
            _uiState.update { it.copy(emailError = false, forgotPassword = true) }
        }
    }

    fun sendPasswordResetLink() {
        _uiState.update {
            it.copy(
                forgotPassword = false,
                loading = true,
                loadingMsg = R.string.sending_password_reset_link
            )
        }
        val auth = Firebase.auth
        auth.sendPasswordResetEmail(uiState.value.email)
            .addOnSuccessListener {
                _uiState.update { state -> state.copy(loading = false, passwordReset = STATE_SUCCESS) }
            }
            .addOnFailureListener {
                _uiState.update { state -> state.copy(loading = false,passwordReset = STATE_ERROR) }
            }
    }

    fun resetPasswordResetState() {
        _uiState.update { state -> state.copy(passwordReset = STATE_LOADING) }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val loading: Boolean = false,
    val loginError: Boolean = false,
    val loginSuccess: Boolean = false,
    val forgotPassword: Boolean = false,
    val loadingMsg: Int = R.string.loading,
    val passwordReset:Int = STATE_LOADING
)