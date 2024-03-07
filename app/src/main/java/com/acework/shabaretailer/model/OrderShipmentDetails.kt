package com.acework.shabaretailer.model

data class OrderShipmentDetails(
    val dispatchConfirmationNumber: String = "",
    val docContent: String = "",
    val docImageFormat: String = "",
    val docTypeCode: String = "",
    val shipmentTrackingNumber: String = ""
)