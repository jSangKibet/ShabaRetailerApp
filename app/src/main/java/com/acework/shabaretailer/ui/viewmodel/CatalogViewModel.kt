package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.model.Retailer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val currentUser = Firebase.auth.currentUser

        if (currentUser != null) {
            _uiState.update { state -> state.copy(user = currentUser) }

            if (currentUser.isEmailVerified) {
                _uiState.update { state -> state.copy(userVerified = true) }
                loadRetailer()
            } else {
                _uiState.update { state -> state.copy(userVerified = false) }
                _uiState.update { state -> state.copy(loading = false) }
            }

        } else {
            _uiState.update { state -> state.copy(loading = false) }
        }
    }

    fun loadRetailer() {
        _uiState.update { state -> state.copy(loading = true, retailerLoading = STATE_LOADING) }

        val db = Firebase.firestore
        db.collection("retailersV2").document(uiState.value.user!!.uid).get()
            .addOnSuccessListener {
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        retailerLoading = STATE_SUCCESS
                    )
                }
                if (it == null) {
                    _uiState.update { state -> state.copy(retailerExists = false) }
                } else {
                    val retailer = it.toObject<Retailer>()
                    if(retailer==null){
                        _uiState.update { state -> state.copy(retailerExists = false) }
                    }else{
                        PostalService.retailer = retailer
                        _uiState.update { state ->
                            state.copy(
                                retailer = retailer,
                                retailerExists = true
                            )
                        }
                    }

                }
            }.addOnFailureListener { exception ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        retailerLoading = STATE_ERROR
                    )
                }
                exception.message?.let { message -> Log.e("FirebaseError", message) }
            }
    }

    fun setShowingBankDetails(value: Boolean) =
        _uiState.update { it.copy(showBankDetails = value) }

    fun setConfirmLoggingOut(value: Boolean) =
        _uiState.update { it.copy(confirmLoggingOut = value) }

    fun logout() {
        Firebase.auth.signOut()
        _uiState.update { it.copy(signedOut = true) }
    }

    fun resendVerificationEmail() {
        uiState.value.user?.let { user ->
            _uiState.update { state ->
                state.copy(
                    loading = true,
                    sendingVerificationEmail = STATE_LOADING
                )
            }

            user.sendEmailVerification().addOnSuccessListener {
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        sendingVerificationEmail = STATE_SUCCESS
                    )
                }
            }.addOnFailureListener { exception ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        sendingVerificationEmail = STATE_ERROR
                    )
                }
                exception.message?.let { message -> Log.e("FirebaseError", message) }
            }
        }
    }

    fun reloadUser() {
        Firebase.auth.currentUser?.let {
            it.reload().addOnSuccessListener {

            }.addOnFailureListener {
                
            }
        }
    }
}

data class CatalogUiState(
    val loading: Boolean = true,

    val user: FirebaseUser? = null,
    val userVerified: Boolean = false,

    val retailer: Retailer = Retailer(),
    val retailerLoading: Int = STATE_LOADING,
    val retailerExists: Boolean = false,

    val sendingVerificationEmail: Int = STATE_LOADING,
    val showBankDetails: Boolean = false,
    val confirmLoggingOut: Boolean = false,
    val signedOut: Boolean = false
)