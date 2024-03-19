package com.acework.shabaretailer.model

class ShippingCosts(val bagTotal: Double, val total: Double) {
    var weightPrice: Double = 0.0
    var duties: Double = 0.0
    var taxes: Double = 0.0
    var dhlFees: Double = 0.0
    var dhlExpressFee: Double = 0.0

    fun calculateWeightPrice() {
        weightPrice = total - (bagTotal + duties + taxes + dhlFees)
    }

    fun getTotalShippingCosts(): Double {
        return total - bagTotal
    }
}