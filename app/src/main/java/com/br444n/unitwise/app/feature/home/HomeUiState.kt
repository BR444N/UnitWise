package com.br444n.unitwise.app.feature.home

import com.br444n.unitwise.app.feature.home.components.ProductInputState

enum class UnitSelectionDriver {
    PRODUCT_A,
    PRODUCT_B
}

data class HomeUiState(
    val productA: ProductInputState = ProductInputState(),
    val productB: ProductInputState = ProductInputState(),
    val isLoading: Boolean = false,
    val incompatibleUnitsToastEvent: Int = 0,
    val unitSelectionDriver: UnitSelectionDriver? = null,
    val editingComparisonId: Int? = null,
    val editingShareId: String? = null,
    val shouldShowOnboarding: Boolean = false,
    val inlineComparisonItemId: Int? = null
) {
    val isCalculateEnabled: Boolean
        get() {
            if (inlineComparisonItemId != null) {
                // If it's inline comparison (list), allow saving if at least one is valid
                return productA.isValid() || productB.isValid()
            }
            // For normal comparison, both must be valid
            return productA.isValid() && productB.isValid()
        }
}

/**
 * Validates that all fields in the ProductInputState are filled with valid data.
 */
fun ProductInputState.isValid(): Boolean {
    val quantityValue = quantity.toIntOrNull()
    return productName.isNotBlank() &&
            contentAmount.isNotBlank() && contentAmount.toDoubleOrNull() != null &&
            price.isNotBlank() && price.toDoubleOrNull() != null &&
            quantity.isNotBlank() && quantityValue != null && quantityValue > 0
}
