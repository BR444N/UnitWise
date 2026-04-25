package com.br444n.unitwise.app.feature.comparison

import com.br444n.unitwise.app.feature.home.components.ProductInputState

data class ComparisonUiState(
    val productA: ProductInputState = ProductInputState(),
    val productB: ProductInputState = ProductInputState(),
    val isProductAWinner: Boolean = true,
    val isTie: Boolean = false,
    val savingsTotal: String = "0.00",
    val monthlySavings: String = "0.00",
    val savingsPerStandardUnit: String = "0.00",
    val standardUnitDesc: String = "100 g",
    val unitPriceA: String = "0.00",
    val unitPriceB: String = "0.00"
) {
    val winningProduct: ProductInputState
        get() = if (isProductAWinner) productA else productB

    val losingProduct: ProductInputState
        get() = if (isProductAWinner) productB else productA
}
