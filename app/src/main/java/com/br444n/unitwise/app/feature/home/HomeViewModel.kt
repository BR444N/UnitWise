package com.br444n.unitwise.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.model.MeasurementUnit
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import com.br444n.unitwise.app.domain.usecase.GetComparisonUseCase
import com.br444n.unitwise.app.domain.usecase.IncompatibleMeasurementUnitsException
import com.br444n.unitwise.app.domain.usecase.SaveComparisonUseCase
import com.br444n.unitwise.app.domain.usecase.CompareProductsUseCase
import com.br444n.unitwise.app.data.local.dao.ShoppingListItemDao
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val saveComparisonUseCase: SaveComparisonUseCase,
    private val getComparisonUseCase: GetComparisonUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val shoppingListItemDao: ShoppingListItemDao,
    private val compareProductsUseCase: CompareProductsUseCase = CompareProductsUseCase()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.isHomeShowcaseCompleted.collect { isCompleted ->
                _uiState.update { it.copy(shouldShowOnboarding = !isCompleted) }
            }
        }
    }

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

    fun loadComparisonForEdit(id: Int) {
        viewModelScope.launch {
            val comparison = getComparisonUseCase(id) ?: return@launch
            _uiState.update {
                it.copy(
                    productA = ProductInputState(
                        productName = comparison.productAName,
                        contentAmount = comparison.productAContent,
                        selectedUnit = comparison.productAUnit,
                        price = comparison.productAPrice,
                        quantity = comparison.productAQuantity
                    ),
                    productB = ProductInputState(
                        productName = comparison.productBName,
                        contentAmount = comparison.productBContent,
                        selectedUnit = comparison.productBUnit,
                        price = comparison.productBPrice,
                        quantity = comparison.productBQuantity
                    ),
                    unitSelectionDriver = null,
                    editingComparisonId = comparison.id,
                    editingShareId = comparison.shareId
                )
            }
        }
    }

    fun calculate(onNavigate: (Int) -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val editingComparisonId = _uiState.value.editingComparisonId
                val id = saveComparisonUseCase(
                    productA = _uiState.value.productA,
                    productB = _uiState.value.productB,
                    comparisonId = editingComparisonId,
                    shareId = _uiState.value.editingShareId
                ).toInt()

                delay(CALCULATION_DELAY)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        productA = ProductInputState(),
                        productB = ProductInputState(),
                        unitSelectionDriver = null,
                        editingComparisonId = null,
                        editingShareId = null
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

    fun calculateInline(itemId: Int, onComplete: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Run comparison directly if both are valid, otherwise it's an orphan product
                val isProductAValid = _uiState.value.productA.isValid()
                val isProductBValid = _uiState.value.productB.isValid()
                
                val result = if (isProductAValid && isProductBValid) {
                    compareProductsUseCase(
                        productA = _uiState.value.productA,
                        productB = _uiState.value.productB
                    )
                } else null
                
                delay(CALCULATION_DELAY) // Simulate work for UX

                // Fetch item, update it, and save back
                val currentItem = shoppingListItemDao.getItemById(itemId).firstOrNull()
                if (currentItem != null) {
                    val updated = currentItem.copy(
                        productAName = _uiState.value.productA.productName.takeIf { isProductAValid } ?: "",
                        productAPrice = _uiState.value.productA.price.takeIf { isProductAValid } ?: "",
                        productAContent = _uiState.value.productA.contentAmount.takeIf { isProductAValid } ?: "",
                        productAUnit = _uiState.value.productA.selectedUnit.takeIf { isProductAValid } ?: "",
                        productAQuantity = _uiState.value.productA.quantity.takeIf { isProductAValid } ?: "",
                        productBName = _uiState.value.productB.productName.takeIf { isProductBValid } ?: "",
                        productBPrice = _uiState.value.productB.price.takeIf { isProductBValid } ?: "",
                        productBContent = _uiState.value.productB.contentAmount.takeIf { isProductBValid } ?: "",
                        productBUnit = _uiState.value.productB.selectedUnit.takeIf { isProductBValid } ?: "",
                        productBQuantity = _uiState.value.productB.quantity.takeIf { isProductBValid } ?: "",
                        isProductAWinner = result?.isProductAWinner,
                        isTie = result?.isTie
                    )
                    shoppingListItemDao.updateItem(updated)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            productA = ProductInputState(),
                            productB = ProductInputState(),
                            unitSelectionDriver = null,
                            inlineComparisonItemId = null
                        )
                    }
                    onComplete()
                }
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

    fun prepareInlineComparison(itemId: Int) {
        viewModelScope.launch {
            val item = shoppingListItemDao.getItemById(itemId).firstOrNull()
            if (item != null) {
                _uiState.update {
                    it.copy(
                        inlineComparisonItemId = itemId,
                        productA = ProductInputState(
                            productName = item.productAName,
                            price = item.productAPrice,
                            contentAmount = item.productAContent,
                            selectedUnit = item.productAUnit.takeIf { u -> u.isNotBlank() } ?: it.productA.selectedUnit,
                            quantity = item.productAQuantity
                        ),
                        productB = ProductInputState(
                            productName = item.productBName,
                            price = item.productBPrice,
                            contentAmount = item.productBContent,
                            selectedUnit = item.productBUnit.takeIf { u -> u.isNotBlank() } ?: it.productB.selectedUnit,
                            quantity = item.productBQuantity
                        )
                    )
                }
            } else {
                _uiState.update {
                    it.copy(inlineComparisonItemId = itemId)
                }
            }
        }
    }

    fun cancelInlineComparison() {
        _uiState.update {
            it.copy(
                inlineComparisonItemId = null,
                productA = ProductInputState(),
                productB = ProductInputState(),
                unitSelectionDriver = null,
                editingComparisonId = null,
                editingShareId = null
            )
        }
    }

    fun completeHomeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.saveHomeShowcaseCompleted(completed = true)
            _uiState.update { it.copy(shouldShowOnboarding = false) }
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
                val userPreferencesRepository = application.container.userPreferencesRepository
                val shoppingListItemDao = application.container.shoppingListItemDao
                HomeViewModel(
                    saveComparisonUseCase = SaveComparisonUseCase(repository),
                    getComparisonUseCase = GetComparisonUseCase(repository),
                    userPreferencesRepository = userPreferencesRepository,
                    shoppingListItemDao = shoppingListItemDao
                )
            }
        }
    }
}
