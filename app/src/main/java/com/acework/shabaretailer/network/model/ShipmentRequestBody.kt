package com.acework.shabaretailer.network.model

import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.getPlannedShippingDateAndTime

data class ShipmentRequestBody(
    val plannedShippingDateAndTime: String = "",
    val pickup: Pickup = Pickup(),
    val productCode: String = "",
    val localProductCode: String = "",
    val accounts: List<Account> = listOf(Account()),
    val customerDetails: CustomerDetails = CustomerDetails(),
    val content: Content = Content()
)

data class Pickup(
    val isRequested: Boolean = true
)

data class Account(
    val typeCode: String = "shipper",
    val number: String = "351403631"
)

data class CustomerDetails(
    val shipperDetails: PartyDetails = PartyDetails(),
    val receiverDetails: PartyDetails = PartyDetails(),
)

data class PartyDetails(
    val postalAddress: PostalAddress = PostalAddress(),
    val contactInformation: ContactInformation = ContactInformation()
)

data class PostalAddress(
    val postalCode: String = "0100",
    val cityName: String = "Nairobi",
    val countryCode: String = "KE",
    val addressLine1: String = "432-00618 Ruaraka"
)

data class ContactInformation(
    val email: String = "team@theshaba.com",
    val phone: String = "+254114286821",
    val companyName: String = "The Shaba",
    val fullName: String = "The Shaba"
)

data class Content(
    val packages: List<Package> = listOf(Package()),
    val isCustomsDeclarable: Boolean = false,
    val description: String = "Sisal handbags",
    val incoterm: String = "DAP",
    val unitOfMeasurement: String = "metric"
)

data class Package(
    val weight: Double = 2.0,
    val dimensions: Dimensions = Dimensions()
)

data class Dimensions(
    val length: Double = 34.0,
    val width: Double = 32.0,
    val height: Double = 34.0
)

fun getShipmentRequestBody(productCodes: Pair<String, String>): ShipmentRequestBody {
    return ShipmentRequestBody(
        plannedShippingDateAndTime = getPlannedShippingDateAndTime(),
        productCode = productCodes.first,
        localProductCode = productCodes.second,
        customerDetails = CustomerDetails(
            receiverDetails = PartyDetails(
                postalAddress = PostalAddress(
                    postalCode = "-",
                    cityName = PostalService.retailer.city,
                    countryCode = PostalService.retailer.countryCode,
                    addressLine1 = "-"
                ),
                contactInformation = ContactInformation(
                    email = PostalService.retailer.email,
                    companyName = PostalService.retailer.name,
                    fullName = PostalService.retailer.name
                )
            )
        )
    )
}