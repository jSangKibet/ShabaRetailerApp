package com.acework.shabaretailer.model

data class Item(
    val color: String = "",
    val description: String = "",
    val features: List<String> = listOf(),
    val insert: String = "",
    val material: String = "",
    val name: String = "Item",
    val price: Int = 0,
    val size: String = "",
    val sku: String = "",
    val strap: String = "",
    val strapLength: String = "",
    val type: String = "",
    val weaving: String = "",
    val weight: Int = 0
)
