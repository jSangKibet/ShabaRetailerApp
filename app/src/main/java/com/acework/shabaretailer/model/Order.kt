package com.acework.shabaretailer.model

data class Order(
    val city: String = "",
    val country: String = "",
    val dispatchConfirmationNumber: String = "",
    val docContent: String = "",
    val docImageFormat: String = "",
    val docTypeCode: String = "",
    val id: String = "",
    val orderItems: List<OrderItem> = listOf(),
    val paymentStatus: Boolean = false,
    val retailerId: String = "",
    val shipmentTrackingNumber: String = "",
    val shipping: Double = 0.0,
    val status: String = "",
    val timestamp: Long = 0
)
