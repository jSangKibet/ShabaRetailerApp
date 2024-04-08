package com.acework.shabaretailer.network.model

import com.acework.shabaretailer.PostalService

fun getShipmentRequestBody(
    bagTotal: Int,
    invoiceDate: String,
    lineItems: List<LineItems>,
    plannedShippingDateAndTime: String
): ShipmentRequestBody {
    return ShipmentRequestBody(
        requestOndemandDeliveryURL = true,
        plannedShippingDateAndTime = plannedShippingDateAndTime,
        pickup = Pickup(
            isRequested = false
        ),
        productCode = "P",
        localProductCode = "P",
        getRateEstimates = false,
        accounts = listOf(
            Accounts(
                typeCode = "shipper",
                number = "351403631"
            )
        ),
        valueAddedServices = listOf(),
        outputImageProperties = OutputImageProperties(
            allDocumentsInOneImage = true,
            encodingFormat = "pdf",
            imageOptions = listOf(
                ImageOptions(
                    typeCode = "invoice",
                    isRequested = true,
                    invoiceType = "commercial",
                    templateName = "COMMERCIAL_INVOICE_P_10"
                ),
                ImageOptions(
                    typeCode = "waybillDoc",
                    fitLabelsToA4 = true,
                    hideAccountNumber = true,
                    templateName = "ARCH_8X4_A4_002"
                ),
                ImageOptions(
                    typeCode = "label",
                    templateName = "ECOM26_84_A4_001"
                )
            )
        ),
        customerDetails = CustomerDetails(
            shipperDetails = ShipperDetails(
                postalAddress = PostalAddress(
                    postalCode = "00100",
                    cityName = "Nairobi",
                    countryCode = "KE",
                    addressLine1 = "P.O Box 432-00618 Ruaraka"
                ),
                contactInformation = ContactInformation(
                    phone = "+254114286821",
                    companyName = "The Shaba",
                    fullName = "Gloria Kisilu"
                )
            ),
            receiverDetails = ReceiverDetails(
                postalAddress = PostalAddress(
                    cityName = PostalService.retailer.city,
                    countryCode = PostalService.retailer.countryCode,
                    postalCode = PostalService.retailer.postalCode,
                    addressLine1 = PostalService.retailer.postalAddress,
                    countryName = PostalService.retailer.country
                ),
                contactInformation = ContactInformation(
                    email = PostalService.retailer.email,
                    phone = PostalService.retailer.number,
                    companyName = PostalService.retailer.businessName,
                    fullName = PostalService.retailer.name
                )
            )
        ),
        content = Content(
            packages = listOf(
                Packages(
                    typeCode = "5BX",
                    weight = 2.0,
                    dimensions = Dimensions(
                        length = 34,
                        width = 32,
                        height = 34
                    )
                )
            ),
            isCustomsDeclarable = true,
            declaredValue = bagTotal,
            declaredValueCurrency = "USD",
            exportDeclaration = ExportDeclaration(
                invoice = Invoice(
                    number = "SHB-INV",
                    date = invoiceDate
                ),
                lineItems = lineItems
            ),
            description = "Shipment",
            incoterm = "DAP",
            unitOfMeasurement = "metric"
        ),
        shipmentNotification = listOf(
            ShipmentNotification(
                typeCode = "email",
                receiverId = "team@theshaba.com",
                languageCode = "eng",
                languageCountryCode = "UK",
                bespokeMessage = "Shipment Notification"
            )
        )
    )
}

data class ShipmentRequestBody(
    val requestOndemandDeliveryURL: Boolean,
    val plannedShippingDateAndTime: String,
    val pickup: Pickup,
    val productCode: String,
    val localProductCode: String,
    val getRateEstimates: Boolean,
    val accounts: List<Accounts>,
    val valueAddedServices: List<String>,
    val outputImageProperties: OutputImageProperties,
    val customerDetails: CustomerDetails,
    val content: Content,
    val shipmentNotification: List<ShipmentNotification>
)

// lvl 1
data class Accounts(
    val typeCode: String,
    val number: String
)

data class Content(
    val packages: List<Packages>,
    val isCustomsDeclarable: Boolean,
    val declaredValue: Int,
    val declaredValueCurrency: String,
    val exportDeclaration: ExportDeclaration,
    val description: String,
    val incoterm: String,
    val unitOfMeasurement: String
)

data class CustomerDetails(
    val shipperDetails: ShipperDetails,
    val receiverDetails: ReceiverDetails
)

data class OutputImageProperties(
    val allDocumentsInOneImage: Boolean,
    val encodingFormat: String,
    val imageOptions: List<ImageOptions>
)

data class Pickup(
    val isRequested: Boolean
)

data class ShipmentNotification(
    val typeCode: String,
    val receiverId: String,
    val languageCode: String,
    val languageCountryCode: String,
    val bespokeMessage: String
)

// lvl 2
data class ExportDeclaration(
    val invoice: Invoice,
    val lineItems: List<LineItems>
)

data class ImageOptions(
    val typeCode: String,
    val templateName: String,
    val isRequested: Boolean? = null,
    val invoiceType: String? = null,
    val fitLabelsToA4: Boolean? = null,
    val hideAccountNumber: Boolean? = null
)

data class Packages(
    val typeCode: String,
    val weight: Double,
    val dimensions: Dimensions
)

data class ReceiverDetails(
    val postalAddress: PostalAddress,
    val contactInformation: ContactInformation
)

data class ShipperDetails(
    val postalAddress: PostalAddress,
    val contactInformation: ContactInformation
)

// lvl 3
data class ContactInformation(
    val email: String? = null,
    val phone: String,
    val companyName: String,
    val fullName: String
)

data class Invoice(
    val number: String,
    val date: String
)

data class LineItems(
    val number: Int,
    val description: String,
    val price: Int,
    val quantity: Quantity,
    val commodityCodes: List<CommodityCodes>,
    val exportReasonType: String,
    val manufacturerCountry: String,
    val weight: Weight,
    val isTaxesPaid: Boolean
)

data class PostalAddress(
    val cityName: String,
    val countryCode: String,
    val postalCode: String,
    val addressLine1: String,
    val countryName: String? = null
)

// lvl 4
data class CommodityCodes(
    val typeCode: String,
    val value: String
)

data class Quantity(
    val value: Int,
    val unitOfMeasurement: String
)

data class Weight(
    val netValue: Double,
    val grossValue: Double
)