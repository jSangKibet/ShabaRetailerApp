package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.countriesWithCodes
import com.acework.shabaretailer.model.Retailer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateRetailerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRetailerUiState())
    val uiState: StateFlow<CreateRetailerUiState> = _uiState.asStateFlow()

    fun updateFields(
        name: String = uiState.value.name,
        country: String = uiState.value.country,
        city: String = uiState.value.city,
    ) {
        _uiState.update { state ->
            state.copy(
                name = name,
                country = country,
                city = city
            )
        }
    }

    fun validate() {
        _uiState.update { state ->
            state.copy(
                nameError = state.name.trim().isEmpty(),
                countryError = state.country.isEmpty(),
                cityError = state.city.trim().isEmpty()
            )
        }

        uiState.value.let { state ->
            if (!(state.nameError || state.countryError || state.cityError)) {
                finishCreatingRetailer()
            }
        }
    }

    private fun finishCreatingRetailer() {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

        currentUser?.let { user ->
            user.email?.let { email ->
                _uiState.update { state ->
                    state.copy(
                        loading = true,
                        retailerCreationStatus = STATE_LOADING
                    )
                }

                val retailer = Retailer(
                    uiState.value.city,
                    uiState.value.country,
                    countriesWithCodes[uiState.value.country] ?: "-",
                    email,
                    user.uid,
                    uiState.value.name,
                )

                db.collection("retailers").document(user.uid).set(retailer)
                    .addOnSuccessListener {
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                retailerCreationStatus = STATE_SUCCESS
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                retailerCreationStatus = STATE_ERROR
                            )
                        }
                        exception.message?.let { message -> Log.e("FirebaseError", message) }
                    }
            }
        }
    }
}

data class CreateRetailerUiState(
    // input values
    val name: String = "",
    val country: String = "",
    val city: String = "",

    // input errors
    val nameError: Boolean = false,
    val countryError: Boolean = false,
    val cityError: Boolean = false,

    // view states
    val loading: Boolean = false,
    val retailerCreationStatus: Int = STATE_LOADING,
)