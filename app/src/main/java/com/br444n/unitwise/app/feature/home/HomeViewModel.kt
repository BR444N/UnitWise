package com.br444n.unitwise.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.model.MeasurementUnit
import com.br444n.unitwise.app.domain.usecase.IncompatibleMeasurementUnitsException
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
            val unitChanged = currentState.productA.selectedUnit != newState.selectedUnit
            val updatedDriver = when {
                !unitChanged -> currentState.unitSelectionDriver
                currentState.unitSelectionDriver == null -> UnitSelectionDriver.PRODUCT_A
                else -> currentState.unitSelectionDriver
            }

            currentState.copy(
                productA = newState,
                productB = if (unitChanged && updatedDriver == UnitSelectionDriver.PRODUCT_A) {
                    ensureCompatibleUnitSelection(
                        driverUnit = newState.selectedUnit,
                        otherState = currentState.productB
                    )
                } else {
                    currentState.productB
                },
                unitSelectionDriver = updatedDriver
            )
        }
    }

    fun updateProductB(newState: ProductInputState) {
        _uiState.update { currentState ->
            val unitChanged = currentState.productB.selectedUnit != newState.selectedUnit
            val updatedDriver = when {
                !unitChanged -> currentState.unitSelectionDriver
                currentState.unitSelectionDriver == null -> UnitSelectionDriver.PRODUCT_B
                else -> currentState.unitSelectionDriver
            }

            currentState.copy(
                productB = newState,
                productA = if (unitChanged && updatedDriver == UnitSelectionDriver.PRODUCT_B) {
                    ensureCompatibleUnitSelection(
                        driverUnit = newState.selectedUnit,
                        otherState = currentState.productA
                    )
                } else {
                    currentState.productA
                },
                unitSelectionDriver = updatedDriver
            )
        }
    }

    fun showIncompatibleUnitsMessage() {
        _uiState.update { currentState ->
            currentState.copy(incompatibleUnitsToastEvent = currentState.incompatibleUnitsToastEvent + 1)
        }
    }

    fun calculate(onNavigate: (Int) -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val id = saveComparisonUseCase(
                    productA = _uiState.value.productA,
                    productB = _uiState.value.productB
                ).toInt()

                delay(CALCULATION_DELAY)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        productA = ProductInputState(),
                        productB = ProductInputState(),
                        unitSelectionDriver = null
                    )
                }
                onNavigate(id)
            } catch (_: IncompatibleMeasurementUnitsException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        incompatibleUnitsToastEvent = it.incompatibleUnitsToastEvent + 1
                    )
                }
            }
        }
    }

    private fun ensureCompatibleUnitSelection(
        driverUnit: String,
        otherState: ProductInputState
    ): ProductInputState {
        if (MeasurementUnit.areCompatible(driverUnit, otherState.selectedUnit)) {
            return otherState
        }

        val fallbackUnit = MeasurementUnit.compatibleUnitsFor(driverUnit).firstOrNull() ?: driverUnit
        return otherState.copy(selectedUnit = fallbackUnit)
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
