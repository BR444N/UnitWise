package com.br444n.unitwise.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.usecase.GetHistoryUseCase
import com.br444n.unitwise.app.domain.usecase.ClearHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... later in the file
class HistoryViewModel(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun clearAll() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getHistoryUseCase().collect { list ->
                val uiModels = list.map { entity ->
                    val priceA = entity.productAPrice.toDoubleOrNull() ?: 0.0
                    val contentA = entity.productAContent.toDoubleOrNull() ?: 1.0
                    val ppuA = if (contentA > 0) priceA / contentA else Double.MAX_VALUE

                    val priceB = entity.productBPrice.toDoubleOrNull() ?: 0.0
                    val contentB = entity.productBContent.toDoubleOrNull() ?: 1.0
                    val ppuB = if (contentB > 0) priceB / contentB else Double.MAX_VALUE

                    val actualWinnerName = when {
                        ppuA < ppuB -> entity.productAName.ifBlank { "Product A" }
                        ppuB < ppuA -> entity.productBName.ifBlank { "Product B" }
                        else -> null
                    }
                    HistoryItemUiModel(entity, actualWinnerName)
                }

                _uiState.update {
                    it.copy(
                        comparisons = uiModels,
                        isLoading = false
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                val repository = application.container.comparisonRepository
                HistoryViewModel(
                    getHistoryUseCase = GetHistoryUseCase(repository),
                    clearHistoryUseCase = ClearHistoryUseCase(repository)
                )
            }
        }
    }
}