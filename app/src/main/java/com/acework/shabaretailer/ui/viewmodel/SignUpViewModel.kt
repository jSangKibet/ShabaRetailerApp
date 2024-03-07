package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun updateEmail(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                payload = currentState.payload.copy(
                    email = value.trim()
                )
            )
        }
    }

    fun updatePassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                payload = currentState.payload.copy(
                    password = value
                )
            )
        }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                payload = currentState.payload.copy(
                    confirmPassword = value
                )
            )
        }
    }

    fun updateTCAccepted(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                payload = currentState.payload.copy(
                    tcAccepted = value
                )
            )
        }
    }

    fun validateInput() {
        val emailError =
            !Patterns.EMAIL_ADDRESS.matcher(_uiState.value.payload.email).matches()
        val passwordError = _uiState.value.payload.password.isEmpty()
        val confirmPasswordError =
            _uiState.value.payload.confirmPassword != _uiState.value.payload.password
        val tcAcceptedError = !_uiState.value.payload.tcAccepted

        var inputHasErrors = false
        if (emailError) inputHasErrors = true
        if (passwordError) inputHasErrors = true
        if (confirmPasswordError) inputHasErrors = true
        if (tcAcceptedError) inputHasErrors = true

        _uiState.update { currentState ->
            currentState.copy(
                signUpFieldErrors = SignUpFieldErrors(
                    emailError,
                    passwordError,
                    confirmPasswordError,
                    tcAcceptedError
                )
            )
        }

        if (!inputHasErrors) {
            _uiState.update { currentState ->
                currentState.copy(
                    currentState = SignUpStates.SIGNING_UP
                )
            }
            signUp(_uiState.value.payload)
        }
    }

    private fun signUp(payload: SignUpPayload) {
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(payload.email, payload.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentState = SignUpStates.SIGN_UP_SUCCEEDED
                        )
                    }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentState = SignUpStates.SIGNUP_FAILED
                        )
                    }
                    task.exception?.message?.let { Log.d("FbE", it) }
                }
            }
    }

    fun signUpFailed() {
        _uiState.update { currentState ->
            currentState.copy(
                currentState = SignUpStates.DATA_ENTRY
            )
        }
    }
}

data class SignUpUiState(
    val currentState: SignUpStates = SignUpStates.DATA_ENTRY,
    val payload: SignUpPayload = SignUpPayload(),
    val signUpFieldErrors: SignUpFieldErrors = SignUpFieldErrors(),
)

data class SignUpPayload(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val tcAccepted: Boolean = false
)

data class SignUpFieldErrors(
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val tcAcceptedError: Boolean = false,
)

enum class SignUpStates {
    DATA_ENTRY, SIGNING_UP, SIGNUP_FAILED, SIGN_UP_SUCCEEDED
}