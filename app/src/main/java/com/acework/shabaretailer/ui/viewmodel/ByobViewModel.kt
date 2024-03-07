package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.R
import com.acework.shabaretailer.atlas.PRICE_TWENDE
import com.acework.shabaretailer.atlas.PRICE_WAHURA
import com.acework.shabaretailer.atlas.STATE_ERROR
import com.acework.shabaretailer.atlas.STATE_LOADING
import com.acework.shabaretailer.atlas.STATE_SUCCESS
import com.acework.shabaretailer.atlas.getPlannedShippingDate
import com.acework.shabaretailer.atlas.getProductCodes
import com.acework.shabaretailer.atlas.getShipmentDetails
import com.acework.shabaretailer.atlas.getShippingCost
import com.acework.shabaretailer.model.Order
import com.acework.shabaretailer.model.OrderItem
import com.acework.shabaretailer.model.OrderShipmentDetails
import com.acework.shabaretailer.network.NetworkOperations
import com.acework.shabaretailer.network.model.getShipmentRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ByobViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ByobUiState())
    val uiState: StateFlow<ByobUiState> = _uiState.asStateFlow()

    // setters
    fun incrementWahura() = _uiState.update { state -> state.copy(wahura = state.wahura + 2) }
    fun decrementWahura() = _uiState.update { state -> state.copy(wahura = state.wahura - 2) }
    fun incrementTwende() = _uiState.update { state -> state.copy(twende = state.twende + 1) }
    fun decrementTwende() = _uiState.update { state -> state.copy(twende = state.twende - 1) }
    fun setSection(value: Int) = _uiState.update { state -> state.copy(currentSection = value) }
    fun decrementWahuraMustard() =
        _uiState.update { state -> state.copy(wahuraMustard = state.wahuraMustard - 1) }

    fun incrementWahuraMustard() =
        _uiState.update { state -> state.copy(wahuraMustard = state.wahuraMustard + 1) }

    fun decrementWahuraDarkBrown() =
        _uiState.update { state -> state.copy(wahuraDarkBrown = state.wahuraDarkBrown - 1) }

    fun incrementWahuraDarkBrown() =
        _uiState.update { state -> state.copy(wahuraDarkBrown = state.wahuraDarkBrown + 1) }

    fun decrementWahuraDustyPink() =
        _uiState.update { state -> state.copy(wahuraDustyPink = state.wahuraDustyPink - 1) }

    fun incrementWahuraDustyPink() =
        _uiState.update { state -> state.copy(wahuraDustyPink = state.wahuraDustyPink + 1) }

    fun decrementWahuraTaupe() =
        _uiState.update { state -> state.copy(wahuraTaupe = state.wahuraTaupe - 1) }

    fun incrementWahuraTaupe() =
        _uiState.update { state -> state.copy(wahuraTaupe = state.wahuraTaupe + 1) }

    fun decrementWahuraBlack() =
        _uiState.update { state -> state.copy(wahuraBlack = state.wahuraBlack - 1) }

    fun incrementWahuraBlack() =
        _uiState.update { state -> state.copy(wahuraBlack = state.wahuraBlack + 1) }


    fun decrementTwendeMustard() =
        _uiState.update { state -> state.copy(twendeMustard = state.twendeMustard - 1) }

    fun incrementTwendeMustard() =
        _uiState.update { state -> state.copy(twendeMustard = state.twendeMustard + 1) }

    fun decrementTwendeDarkBrown() =
        _uiState.update { state -> state.copy(twendeDarkBrown = state.twendeDarkBrown - 1) }

    fun incrementTwendeDarkBrown() =
        _uiState.update { state -> state.copy(twendeDarkBrown = state.twendeDarkBrown + 1) }

    fun decrementTwendeDustyPink() =
        _uiState.update { state -> state.copy(twendeDustyPink = state.twendeDustyPink - 1) }

    fun incrementTwendeDustyPink() =
        _uiState.update { state -> state.copy(twendeDustyPink = state.twendeDustyPink + 1) }

    fun decrementTwendeTaupe() =
        _uiState.update { state -> state.copy(twendeTaupe = state.twendeTaupe - 1) }

    fun incrementTwendeTaupe() =
        _uiState.update { state -> state.copy(twendeTaupe = state.twendeTaupe + 1) }

    fun decrementTwendeBlack() =
        _uiState.update { state -> state.copy(twendeBlack = state.twendeBlack - 1) }

    fun incrementTwendeBlack() =
        _uiState.update { state -> state.copy(twendeBlack = state.twendeBlack + 1) }

    fun updateOrderTcAccepted(value: Boolean) =
        _uiState.update { state -> state.copy(orderTcAccepted = value) }

    fun updateShowBankDetails(value: Boolean) =
        _uiState.update { state -> state.copy(showBankDetails = value) }

    // getters
    fun getTotal(): Int {
        return (PRICE_WAHURA * (uiState.value.wahura / 2)) + PRICE_TWENDE * (uiState.value.twende)
    }

    fun getWahuraPendingInserts(): Int {
        return (uiState.value.wahura / 2) - (
                uiState.value.wahuraMustard +
                        uiState.value.wahuraDarkBrown +
                        uiState.value.wahuraDustyPink +
                        uiState.value.wahuraTaupe +
                        uiState.value.wahuraBlack)
    }

    fun getTwendePendingInserts(): Int {
        return uiState.value.twende - (
                uiState.value.twendeMustard +
                        uiState.value.twendeDarkBrown +
                        uiState.value.twendeDustyPink +
                        uiState.value.twendeTaupe +
                        uiState.value.twendeBlack)
    }

    private fun getOrder(id: String, shipmentDetails: OrderShipmentDetails): Order {
        return Order(
            PostalService.retailer.city,
            PostalService.retailer.country,
            shipmentDetails.dispatchConfirmationNumber,
            shipmentDetails.docContent,
            shipmentDetails.docImageFormat,
            shipmentDetails.docTypeCode,
            id,
            getOrderItems(),
            false,
            PostalService.retailer.id,
            shipmentDetails.shipmentTrackingNumber,
            uiState.value.shipping,
            "Pending",
            System.currentTimeMillis()
        )
    }

    private fun getOrderItems(): MutableList<OrderItem> {
        val orderItems = mutableListOf<OrderItem>()
        val localUiState = uiState.value

        if (localUiState.wahuraMustard > 0) {
            orderItems.add(getWahuraOrderItem("Mustard", localUiState.wahuraMustard))
        }

        if (localUiState.wahuraDarkBrown > 0) {
            orderItems.add(getWahuraOrderItem("Dark Brown", localUiState.wahuraDarkBrown))
        }

        if (localUiState.wahuraDustyPink > 0) {
            orderItems.add(getWahuraOrderItem("Dusty Pink", localUiState.wahuraDustyPink))
        }

        if (localUiState.wahuraTaupe > 0) {
            orderItems.add(getWahuraOrderItem("Taupe", localUiState.wahuraTaupe))
        }

        if (localUiState.wahuraBlack > 0) {
            orderItems.add(getWahuraOrderItem("Black", localUiState.wahuraBlack))
        }

        if (localUiState.twendeMustard > 0) {
            orderItems.add(getTwendeOrderItem("Mustard", localUiState.twendeMustard))
        }

        if (localUiState.twendeDarkBrown > 0) {
            orderItems.add(getTwendeOrderItem("Dark Brown", localUiState.twendeDarkBrown))
        }

        if (localUiState.twendeDustyPink > 0) {
            orderItems.add(getTwendeOrderItem("Dusty Pink", localUiState.twendeDustyPink))
        }

        if (localUiState.twendeTaupe > 0) {
            orderItems.add(getTwendeOrderItem("Taupe", localUiState.twendeTaupe))
        }

        if (localUiState.twendeBlack > 0) {
            orderItems.add(getTwendeOrderItem("Black", localUiState.twendeBlack))
        }

        return orderItems
    }

    private fun getWahuraOrderItem(insertColor: String, quantity: Int): OrderItem {
        return OrderItem(
            insertColor,
            PRICE_WAHURA,
            quantity,
            "3"
        )
    }

    private fun getTwendeOrderItem(insertColor: String, quantity: Int): OrderItem {
        return OrderItem(
            insertColor,
            PRICE_TWENDE,
            quantity,
            "1"
        )
    }

    companion object SECTIONS {
        const val BUILD_BOX = 0
        const val CHOOSE_INSERT_COLORS = 1
        const val CONFIRM_ORDER = 2
    }

    fun errorConsumed() {
        _uiState.update { it.copy(errorPlacingOrder = false) }
    }

    fun getRating() {
        _uiState.update { state ->
            state.copy(
                loading = true,
                loadingMessage = R.string.getting_shipping_rates,
                loadingRates = STATE_LOADING
            )
        }
        viewModelScope.launch {

            val parameters = mapOf(
                "accountNumber" to "351403631",
                "originCountryCode" to "KE",
                "originCityName" to "Nairobi",
                "destinationCountryCode" to PostalService.retailer.countryCode,
                "destinationCityName" to PostalService.retailer.city,
                "weight" to "2",
                "length" to "34",
                "width" to "32",
                "height" to "34",
                "plannedShippingDate" to getPlannedShippingDate(),
                "isCustomsDeclarable" to "false",
                "unitOfMeasurement" to "metric"
            )

            NetworkOperations.rates(parameters) { requestStatus, responseStatus, errorCode, result ->
                _uiState.update { state -> state.copy(loading = false) }
                if (requestStatus && responseStatus) {
                    val shippingFee = getShippingCost(result)
                    val productCodes = getProductCodes(result)
                    if (shippingFee >= 0) {
                        _uiState.update { state ->
                            state.copy(
                                loadingRates = STATE_SUCCESS,
                                shipping = shippingFee,
                                productCodes = productCodes
                            )
                        }
                    } else {
                        _uiState.update { state -> state.copy(loadingRates = STATE_ERROR) }
                    }
                } else {
                    println(errorCode)
                    _uiState.update { state -> state.copy(loadingRates = STATE_ERROR) }
                }
            }
        }
    }

    fun getShipment() {
        _uiState.update { state ->
            state.copy(
                loading = true,
                loadingMessage = R.string.placing_order,
                loadingShipment = STATE_LOADING
            )
        }
        viewModelScope.launch {

            val shipmentRequestBody = getShipmentRequestBody(uiState.value.productCodes)

            NetworkOperations.shipment(shipmentRequestBody) { requestStatus, responseStatus, errorCode, result ->
                if (requestStatus && responseStatus) {
                    val shipmentDetails = getShipmentDetails(result)
                    if (shipmentDetails.dispatchConfirmationNumber.isNotEmpty()) {
                        placeOrder(shipmentDetails)
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                loading = false,
                                loadingShipment = STATE_ERROR
                            )
                        }
                    }
                    _uiState.update { state -> state.copy(loading = false) }
                    println(result)
                    // todo: place order
                } else {
                    println(errorCode)
                    _uiState.update { state ->
                        state.copy(
                            loading = false,
                            loadingShipment = STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun placeOrder(shipmentDetails: OrderShipmentDetails) {
        val db = Firebase.firestore
        val newOrderRef = db.collection("orders").document()
        newOrderRef.set(getOrder(newOrderRef.id, shipmentDetails))
            .addOnSuccessListener {
                _uiState.update { it.copy(loading = false, orderPlaced = true) }
            }
            .addOnFailureListener { e ->
                _uiState.update { s -> s.copy(loading = false, errorPlacingOrder = true) }
                e.message?.let { Log.d("FirebaseError", it) }
            }
    }
}

data class ByobUiState(
    val wahura: Int = 0,
    val twende: Int = 0,
    val currentSection: Int = ByobViewModel.BUILD_BOX,
    val wahuraMustard: Int = 0,
    val wahuraDarkBrown: Int = 0,
    val wahuraDustyPink: Int = 0,
    val wahuraTaupe: Int = 0,
    val wahuraBlack: Int = 0,
    val twendeMustard: Int = 0,
    val twendeDarkBrown: Int = 0,
    val twendeDustyPink: Int = 0,
    val twendeTaupe: Int = 0,
    val twendeBlack: Int = 0,
    val orderTcAccepted: Boolean = false,
    val showBankDetails: Boolean = false,
    val loading: Boolean = false,
    val loadingMessage: Int = R.string.loading,
    val orderPlaced: Boolean = false,
    val errorPlacingOrder: Boolean = false,
    val loadingRates: Int = STATE_LOADING,
    val shipping: Double = 0.0,
    val productCodes: Pair<String, String> = Pair("D", "D"),
    val loadingShipment: Int = STATE_LOADING
)