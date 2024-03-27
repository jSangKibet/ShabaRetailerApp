package com.acework.shabaretailer.model

class ShippingCosts(var bagTotal: Double, var total: Double) {
    constructor() : this(0.0, 0.0)

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