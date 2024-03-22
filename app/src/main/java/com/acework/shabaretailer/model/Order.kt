package com.acework.shabaretailer.model

data class Order(
    val city: String = "",
    val country: String = "",
    val id: String = "",
    val orderItems: List<OrderItem> = listOf(),
    val retailerId: String = "",
    val shipmentDocuments: String = "",
    val shipmentTrackingNumber: String = "",
    val shippingCosts: ShippingCosts = ShippingCosts(0.0, 0.0),
    val shippingDateTime: String = "",
    val status: String = "",
    val timestamp: Long = 0
)
