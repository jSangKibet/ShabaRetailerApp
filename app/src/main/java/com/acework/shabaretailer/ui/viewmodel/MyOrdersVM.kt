package com.acework.shabaretailer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyOrdersVM : ViewModel() {
    private val _uiState = MutableStateFlow(MyOrdersUiState())
    val uiState: StateFlow<MyOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        val database = Firebase.firestore
        database.collection("ordersV2")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereEqualTo("retailerId", PostalService.retailer.id)
            .addSnapshotListener { snapshot, exception ->
                if (exception == null) {
                    val orders = ArrayList<Order>()
                    snapshot?.let {
                        for (document in snapshot) {
                            orders.add(document.toObject())
                        }
                    }
                    _uiState.update {
                        it.copy(
                            loading = true,
                            loadingError = false,
                            orders = orders
                        )
                    }
                } else {
                    _uiState.update { it.copy(loading = false, loadingError = true) }
                    exception.message?.let { message -> Log.e("FirestoreError", message) }
                }
            }
    }
}

data class MyOrdersUiState(
    val loading: Boolean = false,
    val loadingError: Boolean = false,
    val orders: List<Order> = listOf()
)