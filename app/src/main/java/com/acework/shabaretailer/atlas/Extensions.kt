package com.acework.shabaretailer.atlas

import com.acework.shabaretailer.R
import com.acework.shabaretailer.model.Item
import com.acework.shabaretailer.model.Order

fun Item.getImage(page: Int): Int {
    return if (this.sku == "1") {
        when (page) {
            0 -> R.drawable.twende_black
            1 -> R.drawable.twende_mustard
            2 -> R.drawable.twende_maroon
            else -> R.drawable.twende_dark_brown
        }
    } else {
        when (page) {
            0 -> R.drawable.wahura_black
            1 -> R.drawable.wahura_mustard
            2 -> R.drawable.wahura_maroon
            else -> R.drawable.wahura_dark_brown
        }
    }
}

fun Item.getPrice(): Int {
    return if (this.sku == "1") {
        PRICE_TWENDE
    } else {
        PRICE_WAHURA
    }
}

fun Order.getTotal(): Int {
    var total = 0
    for (item in orderItems) {
        total += item.quantity * item.price
    }
    return total
}

fun String.capsWords(): String {
    return this.lowercase().split(" ")
        .joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }
}