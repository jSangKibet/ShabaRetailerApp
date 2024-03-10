package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.countriesWithCodes
import com.acework.shabaretailer.model.Retailer
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditRetailerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditRetailerUiState())
    val uiState: StateFlow<EditRetailerUiState> = _uiState.asStateFlow()

    fun updateFields(
        businessName: String = uiState.value.businessName,
        country: String = uiState.value.country,
        city: String = uiState.value.city,
        postalAddress: String = uiState.value.postalAddress,
        number: String = uiState.value.number,
        name: String = uiState.value.name,
        stateCode: String = uiState.value.stateCode
    ) {
        _uiState.update { state ->
            state.copy(
                businessName = businessName,
                country = country,
                city = city,
                postalAddress = postalAddress,
                number = number,
                name = name,
                stateCode = stateCode
            )
        }
    }


    fun validate() {
        _uiState.update { state ->
            state.copy(
                businessNameError = state.businessName.trim().isEmpty(),
                countryError = state.country.isEmpty(),
                cityError = state.city.trim().isEmpty(),
                postalAddressError = state.postalAddress.trim().isEmpty(),
                numberError = !state.number.trim().matches(Regex("[+]\\d+")),
                nameError = state.name.trim().isEmpty()
            )
        }

        if (uiState.value.isInputValid()) {
            editRetailer()
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

            val retailer = Retailer.create(
                uiState.value.businessName,
                uiState.value.city,
                uiState.value.country,
                countriesWithCodes[uiState.value.country] ?: "-",
                PostalService.retailer.email,
                PostalService.retailer.id,
                uiState.value.name,
                uiState.value.number,
                uiState.value.postalAddress,
                uiState.value.stateCode
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
    val businessName: String = PostalService.retailer.businessName,
    val country: String = PostalService.retailer.country,
    val city: String = PostalService.retailer.city,
    val postalAddress: String = PostalService.retailer.postalAddress,
    val number: String = PostalService.retailer.number,
    val name: String = PostalService.retailer.name,
    val stateCode: String = PostalService.retailer.stateCode,

    // input errors
    val businessNameError: Boolean = false,
    val countryError: Boolean = false,
    val cityError: Boolean = false,
    val postalAddressError: Boolean = false,
    val numberError: Boolean = false,
    val nameError: Boolean = false,

    // view states
    val loading: Boolean = false,
    val retailerEditingStatus: Int = STATE_LOADING,
)

fun EditRetailerUiState.isInputValid(): Boolean {
    if (businessNameError) return false
    if (countryError) return false
    if (cityError) return false
    if (postalAddressError) return false
    if (numberError) return false
    if (nameError) return false
    return true
}