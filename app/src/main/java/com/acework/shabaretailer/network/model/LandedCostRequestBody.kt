package com.acework.shabaretailer.network.model

data class LandedCostRequestBody(
    val customerDetails: LCCustomerDetails = LCCustomerDetails(),
    val accounts: List<LCAccount> = listOf(LCAccount()),
    val productCode: String = "",
    val localProductCode: String = "",
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
    val items: List<LCItem> = listOf(LCItem()),
    val getTariffFormula: Boolean = true,
    val getQuotationID: Boolean = false
)

data class LCCustomerDetails(
    val shipperDetails: LCPartyDetails = LCPartyDetails(),
    val customerDetails: LCPartyDetails = LCPartyDetails()
)

data class LCPartyDetails(
    val postalCode: String = "00100",
    val cityName: String = "NAIROBI",
    val countryCode: String = "KE",
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

data class LCItem(
    val number: Int = 3,
    val name: String = "Wahura Bucket Bag",
    val description: String = "Sisal handbag",
    val manufacturerCountry: String = "KE",
    val partNumber: String = "3",
    val quantity: Int = 4,
    val quantityType: String = "prt",
    val unitPrice: Double = 35.0,
    val unitPriceCurrencyCode: String = "USD",
    val customsValue: Double = 0.0,
    val customsValueCurrencyCode: String = "USD",
    val commodityCode: String = "42022900",
    val weight: Double = 5.0,
    val weightUnitOfMeasurement: String = "metric",
    val category: String = "404",
    val brand: String = "Shaba",
    val goodsCharacteristics: List<Any> = listOf(),
    val additionalQuantityDefinitions: List<Any> = listOf(),
    val estimatedTariffRateType: String = "highest_rate"
)

