package com.br444n.unitwise.app.feature.comparison

import androidx.lifecycle.ViewModel
import com.br444n.unitwise.app.domain.usecase.CompareProductsUseCase
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ComparisonViewModel(
    private val compareProductsUseCase: CompareProductsUseCase = CompareProductsUseCase()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    fun calculate(productA: ProductInputState, productB: ProductInputState) {
        val result = compareProductsUseCase(productA, productB)
        
        _uiState.update { currentState ->
            currentState.copy(
                productA = productA,
                productB = productB,
                isProductAWinner = result.isProductAWinner,
                savingsTotal = result.savingsTotal,
                monthlySavings = result.monthlySavings,
                savingsPerStandardUnit = result.savingsPerStandardUnit,
                standardUnitDesc = result.standardUnitDesc
            )
        }
    }
}