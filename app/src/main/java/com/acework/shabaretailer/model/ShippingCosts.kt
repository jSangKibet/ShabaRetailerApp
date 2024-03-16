package com.acework.shabaretailer.model

class ShippingCosts(val bagTotal: Double) {
    var weightPrice: Double = 0.0
    var duties: Double = 0.0
    var taxes: Double = 0.0
    var dhlFees: Double = 0.0
    var dhlExpressFee: Double = 0.0
    var total: Double = 0.0

    fun calculateTotal() {
        total = bagTotal + weightPrice + duties + taxes + dhlFees + dhlExpressFee
    }
}