package com.acework.shabaretailer.network.model

data class ShipmentRequestBody(
    var requestOndemandDeliveryURL: Boolean? = null,
    var plannedShippingDateAndTime: String? = null,
    var pickup: Pickup? = Pickup(),
    var productCode: String? = null,
    var localProductCode: String? = null,
    var getRateEstimates: Boolean? = null,
    var accounts: ArrayList<Accounts> = arrayListOf(),
    var valueAddedServices: ArrayList<String> = arrayListOf(),
    var outputImageProperties: OutputImageProperties? = OutputImageProperties(),
    var customerDetails: CustomerDetails? = CustomerDetails(),
    var content: Content? = Content(),
    var shipmentNotification: ArrayList<ShipmentNotification> = arrayListOf()
)

data class Pickup(
    var isRequested: Boolean? = null
)

data class Accounts (

    @SerializedName("typeCode" ) var typeCode : String? = null,
    @SerializedName("number"   ) var number   : String? = null

)