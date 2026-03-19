package com.br444n.unitwise.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
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

    fun calculate(onNavigate: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(3000) // Simulate calculation delay for 1.5s
            _uiState.update { it.copy(isLoading = false) }
            onNavigate()
        }
    }
}
