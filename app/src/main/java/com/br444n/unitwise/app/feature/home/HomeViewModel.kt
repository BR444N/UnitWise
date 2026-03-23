package com.br444n.unitwise.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.usecase.SaveComparisonUseCase
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val saveComparisonUseCase: SaveComparisonUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateProductA(newState: ProductInputState) {
        _uiState.update { currentState ->
            currentState.copy(productA = newState)
        }
    }

    fun updateProductB(newState: ProductInputState) {
        _uiState.update { currentState ->
            currentState.copy(productB = newState)
        }
    }

    fun calculate(onNavigate: (Int) -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Save to database
            val id = saveComparisonUseCase(
                productA = _uiState.value.productA,
                productB = _uiState.value.productB
            ).toInt()

            delay(CALCULATION_DELAY) // Simulate calculation delay
            
            // Clear the inputs returning to initial defaults but empty strings!
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    productA = ProductInputState(),
                    productB = ProductInputState()
                )
            }
            onNavigate(id)
        }
    }

    companion object {
        private const val CALCULATION_DELAY = 1500L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                val repository = application.container.comparisonRepository
                HomeViewModel(SaveComparisonUseCase(repository))
            }
        }
    }
}
