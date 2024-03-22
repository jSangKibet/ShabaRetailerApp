package com.acework.shabaretailer.network.model

import com.acework.shabaretailer.PostalService
import com.acework.shabaretailer.atlas.PRICE_TWENDE
import com.acework.shabaretailer.atlas.PRICE_WAHURA

data class LandedCostRequestBody(
    val customerDetails: LCCustomerDetails = LCCustomerDetails(),
    val accounts: List<LCAccount> = listOf(LCAccount()),
    val productCode: String = "P",
    val localProductCode: String = "P",
    val unitOfMeasurement: String = "metric",
    val currencyCode: String = "USD",
    val isCustomsDeclarable: Boolean = true,
    val isDTPRequested: Boolean = true,
    val isInsuranceRequested: Boolean = false,
    val getCostBreakdown: Boolean = true,
    val charges: List<Any> = listOf(),
    val shipmentPurpose: String = "commercial",
    val transportationMode: String = "air",
    val merchantSelectedCarrierName: String = "DHL",
    val packages: List<LCPackage> = listOf(LCPackage()),
    val items: List<LCItem> = listOf(),
    val getTariffFormula: Boolean = true,
    val getQuotationID: Boolean = false
) {
    companion object {
        fun create(
            numOfTwende: Int,
            numOfWahura: Int
        ): LandedCostRequestBody {
            return LandedCostRequestBody(
                customerDetails = LCCustomerDetails(
                    receiverDetails =
                    if (PostalService.retailer.stateCode.isNotEmpty()) {
                        LCPartyDetailsStateCode(
                            postalCode = "",
                            cityName = PostalService.retailer.city,
                            countryCode = PostalService.retailer.countryCode,
                            provinceCode = PostalService.retailer.stateCode
                        )
                    } else {
                        LCPartyDetails(
                            postalCode = "",
                            cityName = PostalService.retailer.city,
                            countryCode = PostalService.retailer.countryCode
                        )
                    }
                ),
                items = listOf(
                    getLCItemTwende(numOfTwende),
                    getLCItemWahura(numOfWahura)
                )
            )
        }
    }
}

data class LCCustomerDetails(
    val shipperDetails: LCPartyDetails = LCPartyDetails(),
    val receiverDetails: LCPartyDetails = LCPartyDetails()
)

open class LCPartyDetails(
    val postalCode: String = "00100",
    val cityName: String = "NAIROBI",
    val countryCode: String = "KE"
)

class LCPartyDetailsStateCode(
    postalCode: String,
    cityName: String,
    countryCode: String,
    val provinceCode: String = ""
) : LCPartyDetails(
    postalCode = postalCode,
    cityName = cityName,
    countryCode = countryCode
)

data class LCAccount(
    val typeCode: String = "shipper",
    val number: String = "351403631"
)

data class LCPackage(
    val typeCode: String = "3BX",
    val weight: Double = 2.0,
    val dimensions: Dimensions = Dimensions()
)

data class Dimensions(
    val length: Int = 34,
    val width: Int = 32,
    val height: Int = 34
)

data class LCItem(
    val number: Int = 0,
    val name: String = "",
    val description: String = "Sisal handbag",
    val manufacturerCountry: String = "KE",
    val partNumber: String = "3",
    val quantity: Int = 4,
    val quantityType: String = "prt",
    val unitPrice: Double = 0.0,
    val unitPriceCurrencyCode: String = "USD",
    val customsValue: Double = 0.0,
    val customsValueCurrencyCode: String = "USD",
    val commodityCode: String = "42022900",
    val weight: Double = 0.0,
    val weightUnitOfMeasurement: String = "metric",
    val category: String = "404",
    val brand: String = "Shaba",
    val goodsCharacteristics: List<Any> = listOf(),
    val additionalQuantityDefinitions: List<Any> = listOf(),
    val estimatedTariffRateType: String = "highest_rate"
)

fun getLCItemTwende(quantity: Int): LCItem {
    return LCItem(
        number = 1,
        name = "Twende Sling Bag",
        quantity = quantity,
        unitPrice = PRICE_TWENDE.toDouble(),
        weight = 0.213
    )
}

fun getLCItemWahura(quantity: Int): LCItem {
    return LCItem(
        number = 2,
        name = "Wahura Bucket Bag",
        quantity = quantity,
        unitPrice = PRICE_WAHURA.toDouble(),
        weight = 0.424
    )
}