package com.br444n.unitwise.app.domain.model

data class ProductInputState(
    val productName: String = "",
    val contentAmount: String = "",
    val selectedUnit: String = "g",
    val price: String = "",
    val quantity: String = "1"
)
