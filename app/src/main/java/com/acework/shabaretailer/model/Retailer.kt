package com.acework.shabaretailer.model

data class Retailer(
    val businessName: String = "",
    val city: String = "",
    val country: String = "",
    val countryCode: String = "",
    val email: String = "",
    val id: String = "",
    val name: String = "",
    val number: String = "",
    val postalCode: String = "",
    val postalAddress: String = "",
    val stateCode: String="",
) {
    companion object {
        fun create(
            businessName: String,
            city: String,
            country: String,
            countryCode: String,
            email: String,
            id: String,
            name: String,
            number: String,
            postalCode: String,
            postalAddress: String,
            stateCode: String
        ): Retailer {
            return Retailer(
                businessName,
                city,
                country,
                countryCode,
                email,
                id,
                name,
                number,
                postalCode,
                postalAddress,
                stateCode
            )
        }
    }
}