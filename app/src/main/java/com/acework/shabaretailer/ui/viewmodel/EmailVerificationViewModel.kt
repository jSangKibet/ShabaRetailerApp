package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EmailVerificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val currentUser = Firebase.auth.currentUser

        if (currentUser != null) {
            _uiState.update { state ->
                state.copy(
                    user = currentUser,
                    userVerified = currentUser.isEmailVerified
                )
            }
        }
    }

    fun sendVerificationEmail() {
        uiState.value.user?.let { user ->
            _uiState.update { state ->
                state.copy(
                    loading = true,
                    loadingMessage = R.string.sending_verification_email
                )
            }

            user.sendEmailVerification().addOnSuccessListener {
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        emailSent = STATE_SUCCESS
                    )
                }
            }.addOnFailureListener { exception ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        emailSent = STATE_ERROR
                    )
                }
                exception.message?.let { message -> Log.e("FirebaseError", message) }
            }
        }
    }

    fun reloadUser() {
        uiState.value.user?.let { user ->
            _uiState.update { state ->
                state.copy(
                    loading = true,
                    loadingMessage = R.string.checking_verification_status
                )
            }

            user.reload()
                .addOnSuccessListener {
                    _uiState.update { state -> state.copy(loading = false) }
                    loadUser()
                }
                .addOnFailureListener {exception->
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            userReloaded = STATE_ERROR
                        )
                    }
                    exception.message?.let { message -> Log.e("FirebaseError", message) }
                }
        }
    }

    fun emailSentAcknowledged() {
        _uiState.update { state -> state.copy(emailSent = STATE_LOADING) }
    }

    fun userReloadedAcknowledged() {
        _uiState.update { state -> state.copy(userReloaded = STATE_LOADING) }
    }
}

data class EmailVerificationUiState(
    val user: FirebaseUser? = null,
    val loading: Boolean = false,
    val loadingMessage: Int = R.string.loading,
    val emailSent: Int = STATE_LOADING,
    val userReloaded: Int = STATE_LOADING,
    val userVerified: Boolean = false
)