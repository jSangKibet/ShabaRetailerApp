package com.acework.shabaretailer.atlas

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.acework.shabaretailer.model.OrderShipmentDetails
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

/**
 * This function returns the product from a json string
 * default values represent worldwide shipping
 */
fun getProductCodes(jsonResult: String): Pair<String, String> {
    return try {
        val gson = Gson()
        val resultAsJson = gson.fromJson(jsonResult, JsonObject::class.java)
        val productArray = resultAsJson.getAsJsonArray("products")
        val product = productArray.get(0).asJsonObject
        val productCode = product.get("productCode").asString
        val localProductCode = product.get("localProductCode").asString
        Pair(productCode, localProductCode)
    } catch (e: Exception) {
        Log.e("StringThatFailedParse", jsonResult)
        e.printStackTrace()
        Pair("D", "D")
    }
}


/**
 * This function returns shipment from a json string
 */
fun getShipmentDetails(jsonResult: String): OrderShipmentDetails {
    return try {
        val gson = Gson()
        val resultAsJson = gson.fromJson(jsonResult, JsonObject::class.java)

        val shipmentTrackingNumber = resultAsJson.get("shipmentTrackingNumber").asString
        val dispatchConfirmationNumber = resultAsJson.get("dispatchConfirmationNumber").asString
        val documents = resultAsJson.getAsJsonArray("documents")
        val document = documents.get(0).asJsonObject
        val docContent = document.get("content").asString
        val docImageFormat = document.get("imageFormat").asString
        val docTypeCode = document.get("typeCode").asString

        OrderShipmentDetails(
            dispatchConfirmationNumber,
            docContent,
            docImageFormat,
            docTypeCode,
            shipmentTrackingNumber
        )
    } catch (e: Exception) {
        Log.e("StringThatFailedParse", jsonResult)
        e.printStackTrace()
        OrderShipmentDetails()
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