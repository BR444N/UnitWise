package com.br444n.unitwise.app.feature.home

import com.br444n.unitwise.app.feature.home.components.ProductInputState

data class HomeUiState(
    val productA: ProductInputState = ProductInputState(),
    val productB: ProductInputState = ProductInputState()
) {
    val isCalculateEnabled: Boolean
        get() = productA.isValid() && productB.isValid()
}

/**
 * Validates that all fields in the ProductInputState are filled with valid data.
 */
fun ProductInputState.isValid(): Boolean {
    return productName.isNotBlank() &&
            contentAmount.isNotBlank() && contentAmount.toDoubleOrNull() != null &&
            price.isNotBlank() && price.toDoubleOrNull() != null &&
            quantity.isNotBlank() && quantity.toIntOrNull() != null
}
