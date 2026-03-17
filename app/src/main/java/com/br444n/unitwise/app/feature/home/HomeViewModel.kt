package com.br444n.unitwise.app.feature.home

import androidx.lifecycle.ViewModel
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
}
