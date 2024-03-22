package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.acework.shabaretailer.R
import com.acework.shabaretailer.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

class OrderVM(private val orderId: String) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())

    init {
        loadOrder()
    }

    fun loadOrder() {
        val database = Firebase.firestore
        _uiState.update {
            it.copy(
                loading = true,
                loadingError = false,
            )
        }
        database.collection("orders").document(orderId)
            .addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    snapshot?.let {
                        val fetchedOrder = snapshot.toObject<Order?>()
                        fetchedOrder?.let { _uiState.update { it.copy(order = fetchedOrder) } }
                    }
                    _uiState.update {
                        it.copy(
                            loading = false,
                            loadingError = snapshot == null,
                        )
                    }
                } else {
                    _uiState.update { it.copy(loading = false, loadingError = true) }
                    exception.message?.let { message -> Log.e("FirestoreError", message) }
                }
            }
    }

    fun getInsertDrawable(insertColor: String): Int {
        if (insertColor == "Mustard") return R.drawable.mustard_circle
        if (insertColor == "Dark Brown") return R.drawable.dark_brown_circle
        if (insertColor == "Dusty Pink") return R.drawable.dusty_pink_circle
        if (insertColor == "Taupe") return R.drawable.taupe_circle
        return R.drawable.black_circle
    }

    fun orderActionClicked() {
        uiState.value.order.status.let { status ->
            when (status) {
                "Pending" -> _uiState.update { state -> state.copy(confirmOrderCancellation = true) }
                "Canceled" -> _uiState.update { state -> state.copy(confirmOrderRestoration = true) }
                "Dispatched" -> _uiState.update { state -> state.copy(confirmOrderReception = true) }
                else -> {}
            }
        }
    }

    fun orderCancellationRevoked() =
        _uiState.update { state -> state.copy(confirmOrderCancellation = false) }

    fun cancelOrder() {
        _uiState.update { state -> state.copy(loading = true, confirmOrderCancellation = false) }
        val database = Firebase.firestore
        database.collection("orders").document(orderId).update("status", "Canceled")
            .addOnCompleteListener {
                _uiState.update { state -> state.copy(loading = false, orderUpdated = true) }
            }
            .addOnFailureListener { exception ->
                _uiState.update { state -> state.copy(loading = false, updateError = true) }
                exception.message?.let { msg -> Log.e("FirebaseError", msg) }
            }
    }

    fun orderRestorationRevoked() =
        _uiState.update { state -> state.copy(confirmOrderRestoration = false) }

    fun restoreOrder() {
        _uiState.update { state -> state.copy(loading = true, confirmOrderRestoration = false) }
        val database = Firebase.firestore
        database.collection("orders").document(orderId).update("status", "Pending")
            .addOnCompleteListener {
                _uiState.update { state -> state.copy(loading = false, orderUpdated = true) }
            }
            .addOnFailureListener { exception ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        updateError = true,
                        orderUpdated = true
                    )
                }
                exception.message?.let { msg -> Log.e("FirebaseError", msg) }
            }
    }

    fun orderReceptionRevoked() =
        _uiState.update { state -> state.copy(confirmOrderReception = false) }

    fun orderReceived() {
        _uiState.update { state -> state.copy(loading = true, confirmOrderReception = false) }
        val database = Firebase.firestore
        database.collection("orders").document(orderId).update("status", "Received")
            .addOnCompleteListener {
                _uiState.update { state -> state.copy(loading = false, orderUpdated = true) }
            }
            .addOnFailureListener { exception ->
                _uiState.update { state ->
                    state.copy(
                        loading = false,
                        updateError = true,
                        orderUpdated = true
                    )
                }
                exception.message?.let { msg -> Log.e("FirebaseError", msg) }
            }
    }

    fun snackbarDisplayed() =
        _uiState.update { state -> state.copy(orderUpdated = false, updateError = false) }
}

data class OrderUiState(
    val loading: Boolean = false,
    val loadingError: Boolean = false,
    val order: Order = Order(),
    val updateError: Boolean = false,
    val orderUpdated: Boolean = false,
    val confirmOrderCancellation: Boolean = false,
    val confirmOrderRestoration: Boolean = false,
    val confirmOrderReception: Boolean = false
)

class OrderVMFactory(orderId: String) {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return OrderVM(orderId) as T
        }
    }
}