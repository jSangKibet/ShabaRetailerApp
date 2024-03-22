package com.acework.shabaretailer.atlas

import android.util.Log
import com.acework.shabaretailer.model.ShippingCosts
import com.acework.shabaretailer.network.model.CommodityCodes
import com.acework.shabaretailer.network.model.LineItems
import com.acework.shabaretailer.network.model.Quantity
import com.acework.shabaretailer.network.model.Weight
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val USD_TO_KSH = 150.0

fun getItemName(sku: String): String {
    if (sku == "1") return "Twende sling bag"
    return if (sku == "3") "Wahura bucket bag" else "Unknown bag"
}

/**
 * This function returns the shipping price from a json string
 * Any value 0+ is valid
 * Any value less than 0 is invalid
 */
fun getShippingCost(jsonResult: String): Double {
    return try {
        val gson = Gson()
        val resultAsJson = gson.fromJson(jsonResult, JsonObject::class.java)
        val productArray = resultAsJson.getAsJsonArray("products")
        val productElement = productArray.get(0)
        val priceArray = productElement.asJsonObject.getAsJsonArray("totalPrice")
        val priceElement = priceArray.get(0)
        val priceField = priceElement.asJsonObject.get("price")
        val priceKes = priceField.asString.toDouble()
        priceKes / USD_TO_KSH
    } catch (e: Exception) {
        Log.e("StringThatFailedParse", jsonResult)
        e.printStackTrace()
        -1.0
    }
}

fun getShipmentDetails(jsonResult: String): Pair<String, String> {
    return try {
        val gson = Gson()
        val resultAsJson = gson.fromJson(jsonResult, JsonObject::class.java)

        val shipmentTrackingNumber = resultAsJson.get("shipmentTrackingNumber").asString

        val documents = resultAsJson.getAsJsonArray("documents")
        val documentObject = documents.get(0).asJsonObject
        val document = documentObject.get("content").asString

        Pair(shipmentTrackingNumber, document)
    } catch (e: Exception) {
        Log.e("StringThatFailedParse", jsonResult)
        e.printStackTrace()
        Pair("", "")
    }
}

// This function just gets a date two days after today
fun getPlannedShippingDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, 2)
    val date = calendar.time
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(date)
}

fun getPlannedShippingDateAndTime(): String {
    return getPlannedShippingDate() + "T09:00:00 GMT+03:00"
}

/**
 * This function returns the shipping costs from a landed cost JSON result
 */
fun getShippingCosts(bagTotal: Double, landedCost: String): ShippingCosts {
    return try {
        val gson = Gson()
        val resultAsJson = gson.fromJson(landedCost, JsonObject::class.java)

        val productArray = resultAsJson.getAsJsonArray("products")
        val product = productArray.get(0).asJsonObject

        val totalPriceArray = product.get("totalPrice").asJsonArray
        val totalPriceObject = totalPriceArray.get(0).asJsonObject
        val totalPrice = totalPriceObject.get("price").asString.toDouble()

        val detailedPriceBreakdown = product.get("detailedPriceBreakdown").asJsonArray
        val detailedPriceBreakdownObject = detailedPriceBreakdown.get(0).asJsonObject
        val breakdown = detailedPriceBreakdownObject.getAsJsonArray("breakdown")

        val shippingCosts = ShippingCosts(bagTotal, totalPrice)

        for (element in breakdown) {
            val obj = element.asJsonObject
            val name = obj.get("name").asString
            val price = obj.get("price").asDouble

            when (name) {
                "STSCH" -> shippingCosts.dhlExpressFee = price
                "TOTAL DUTIES" -> shippingCosts.duties = price
                "TOTAL TAXES" -> shippingCosts.taxes = price
                "TOTAL FEES" -> shippingCosts.dhlFees = price
            }
        }

        shippingCosts.calculateWeightPrice()
        shippingCosts
    } catch (e: Exception) {
        Log.e("StringThatFailedParse", landedCost)
        e.printStackTrace()
        ShippingCosts(0.0, 0.0)
    }
}

fun getWahuraLineItem(quantity: Int): LineItems {
    return getLineItem(
        "Wahura Bucket Bag",
        1,
        PRICE_WAHURA,
        quantity
    )
}

fun getTwendeLineItem(quantity: Int): LineItems {
    return getLineItem(
        "Twende Sling Bag",
        2,
        PRICE_TWENDE,
        quantity
    )
}

fun getLineItem(
    name: String,
    number: Int,
    price: Int,
    quantity: Int
): LineItems {
    return LineItems(
        number = number,
        description = name,
        price = price,
        quantity = Quantity(
            value = quantity,
            unitOfMeasurement = "PCS"
        ),
        commodityCodes = listOf(
            CommodityCodes(
                typeCode = "outbound",
                value = "420221"
            )
        ),
        exportReasonType = "commercial_purpose_or_sale",
        manufacturerCountry = "KE",
        weight = Weight(
            netValue = 0.9,
            grossValue = 0.9
        ),
        isTaxesPaid = true
    )
}