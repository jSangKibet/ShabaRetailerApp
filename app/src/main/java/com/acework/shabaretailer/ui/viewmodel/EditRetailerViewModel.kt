package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.countriesWithCodes
import com.acework.shabaretailer.model.Retailer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditRetailerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditRetailerUiState())
    val uiState: StateFlow<EditRetailerUiState> = _uiState.asStateFlow()

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
                editRetailer()
            }
        }
    }

    private fun editRetailer() {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

        currentUser?.let { user ->
            _uiState.update { state ->
                state.copy(
                    loading = true,
                    retailerEditingStatus = STATE_LOADING
                )
            }

            val retailer = Retailer(
                uiState.value.city,
                uiState.value.country,
                countriesWithCodes[uiState.value.country] ?: "-",
                PostalService.retailer.email,
                PostalService.retailer.id,
                uiState.value.name,
            )

            db.collection("retailers").document(user.uid).set(retailer)
                .addOnSuccessListener {
                    _uiState.update { s ->
                        s.copy(
                            loading = false,
                            retailerEditingStatus = STATE_SUCCESS
                        )
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update { s ->
                        s.copy(
                            loading = false,
                            retailerEditingStatus = STATE_ERROR
                        )
                    }
                    e.message?.let { message -> Log.e("FirebaseError", message) }
                }
        }
    }
}

data class EditRetailerUiState(
    // input values
    val name: String = PostalService.retailer.name,
    val country: String = PostalService.retailer.country,
    val city: String = PostalService.retailer.city,

    // input errors
    val nameError: Boolean = false,
    val countryError: Boolean = false,
    val cityError: Boolean = false,

    // view states
    val loading: Boolean = false,
    val retailerEditingStatus: Int = STATE_LOADING
)