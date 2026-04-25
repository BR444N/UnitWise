package com.br444n.unitwise.app.feature.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.usecase.CompareProductsUseCase
import com.br444n.unitwise.app.domain.usecase.GetComparisonByShareIdUseCase
import com.br444n.unitwise.app.domain.usecase.GetComparisonUseCase
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.br444n.unitwise.app.feature.share.SharedComparisonData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComparisonViewModel(
    private val getComparisonUseCase: GetComparisonUseCase,
    private val getComparisonByShareIdUseCase: GetComparisonByShareIdUseCase,
    private val compareProductsUseCase: CompareProductsUseCase = CompareProductsUseCase()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    fun loadComparison(id: Int) {
        viewModelScope.launch {
            val entity = getComparisonUseCase(id) ?: return@launch
            showComparison(
                productA = ProductInputState(
                    productName = entity.productAName,
                    contentAmount = entity.productAContent,
                    selectedUnit = entity.productAUnit,
                    price = entity.productAPrice,
                    quantity = entity.productAQuantity
                ),
                productB = ProductInputState(
                    productName = entity.productBName,
                    contentAmount = entity.productBContent,
                    selectedUnit = entity.productBUnit,
                    price = entity.productBPrice,
                    quantity = entity.productBQuantity
                )
            )
        }
    }

    fun loadComparisonByShareId(shareId: String, encryptionKey: String?) {
        viewModelScope.launch {
            val entity = getComparisonByShareIdUseCase(shareId, encryptionKey) ?: return@launch
            showComparison(
                productA = ProductInputState(
                    productName = entity.productAName,
                    contentAmount = entity.productAContent,
                    selectedUnit = entity.productAUnit,
                    price = entity.productAPrice,
                    quantity = entity.productAQuantity
                ),
                productB = ProductInputState(
                    productName = entity.productBName,
                    contentAmount = entity.productBContent,
                    selectedUnit = entity.productBUnit,
                    price = entity.productBPrice,
                    quantity = entity.productBQuantity
                )
            )
        }
    }

    fun loadSharedComparison(sharedComparisonData: SharedComparisonData) {
        showComparison(
            productA = sharedComparisonData.productA,
            productB = sharedComparisonData.productB
        )
    }

    private fun showComparison(
        productA: ProductInputState,
        productB: ProductInputState
    ) {
        val result = compareProductsUseCase(productA, productB)

        _uiState.update { currentState ->
            currentState.copy(
                productA = productA,
                productB = productB,
                isProductAWinner = result.isProductAWinner,
                isTie = result.isTie,
                savingsTotal = result.savingsTotal,
                monthlySavings = result.monthlySavings,
                savingsPerStandardUnit = result.savingsPerStandardUnit,
                standardUnitDesc = result.standardUnitDesc,
                unitPriceA = result.unitPriceA,
                unitPriceB = result.unitPriceB
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                val repository = application.container.comparisonRepository
                ComparisonViewModel(
                    getComparisonUseCase = GetComparisonUseCase(repository),
                    getComparisonByShareIdUseCase = GetComparisonByShareIdUseCase(repository)
                )
            }
        }
    }
}
