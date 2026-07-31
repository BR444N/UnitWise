package com.br444n.unitwise.app.domain.model

const val PRODUCT_NAME_MAX_LENGTH = 24
const val CONTENT_AMOUNT_MAX_LENGTH = 7
const val PRICE_MAX_LENGTH = 7
const val QUANTITY_MAX_LENGTH = 3

data class ProductInputState(
    val productName: String = "",
    val contentAmount: String = "",
    val selectedUnit: String = "g",
    val price: String = "",
    val quantity: String = "1",
)
