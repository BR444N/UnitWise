package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.core.firebase.domain.usecase.LogParityToggledUseCase
import com.br444n.unitwise.app.core.utils.PriceUtils
import com.br444n.unitwise.app.data.local.dao.ShoppingListDao
import com.br444n.unitwise.app.data.local.dao.ShoppingListItemDao
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val shoppingListDao: ShoppingListDao,
    private val shoppingListItemDao: ShoppingListItemDao,
    private val logParityToggled: LogParityToggledUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val listId: Int = checkNotNull(savedStateHandle["listId"])

    val uiState: StateFlow<ShoppingListDetailsUiState> =
        combine(
            shoppingListDao.getListById(listId),
            shoppingListItemDao.getItemsForList(listId),
        ) { listEntity, items ->
            var fairA = 0.0
            var fairB = 0.0
            var absoluteA = 0.0
            var absoluteB = 0.0
            var smart = 0.0
            var hasOrphansFlag = false

            items.forEach { item ->
                val priceA =
                    if (item.productAPrice.isNotBlank()) {
                        PriceUtils.parsePrice(
                            item.productAPrice,
                        )
                    } else {
                        0.0
                    }
                val priceB =
                    if (item.productBPrice.isNotBlank()) {
                        PriceUtils.parsePrice(
                            item.productBPrice,
                        )
                    } else {
                        0.0
                    }

                val isAValid = item.productAName.isNotBlank() && item.productAPrice.isNotBlank()
                val isBValid = item.productBName.isNotBlank() && item.productBPrice.isNotBlank()

                // Absolute totals (includes orphans)
                if (isAValid) absoluteA += priceA
                if (isBValid) absoluteB += priceB

                if (isAValid && !isBValid) hasOrphansFlag = true
                if (!isAValid && isBValid) hasOrphansFlag = true

                // Fair totals (only when both are compared)
                if (item.isProductAWinner != null || item.isTie == true) {
                    fairA += priceA
                    fairB += priceB

                    smart +=
                        if (item.isProductAWinner == true || item.isTie == true) {
                            priceA
                        } else {
                            priceB
                        }
                }
            }

            ShoppingListDetailsUiState(
                listName = listEntity?.name ?: "",
                items = items,
                isLoading = false,
                fairTotalA = fairA,
                fairTotalB = fairB,
                totalWithOrphansA = absoluteA,
                totalWithOrphansB = absoluteB,
                smartTotal = smart,
                hasOrphans = hasOrphansFlag,
                error = null,
            )
        }.catch { e -> emit(ShoppingListDetailsUiState(error = e.message, isLoading = false)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ShoppingListDetailsUiState(isLoading = true),
            )

    val seenFeatures: StateFlow<Set<String>> =
        userPreferencesRepository.seenFeatures
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet(),
            )

    fun markFeatureAsSeen(featureKey: String) {
        viewModelScope.launch {
            userPreferencesRepository.markFeatureAsSeen(featureKey)
        }
    }

    fun addItem(categoryName: String) {
        viewModelScope.launch {
            val newItem =
                ShoppingListItemEntity(
                    listId = listId,
                    categoryName = categoryName,
                )
            shoppingListItemDao.insertItem(newItem)
        }
    }

    fun onOrphanCardToggled(isExpanded: Boolean) {
        logParityToggled(isExpanded)
    }

    fun deleteItems(ids: Set<Int>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            shoppingListItemDao.deleteItems(ids)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                    val shoppingListDao = application.container.shoppingListDao
                    val shoppingListItemDao = application.container.shoppingListItemDao
                    // SavedStateHandle is automatically injected via the CreationExtras
                    val savedStateHandle = createSavedStateHandle()
                    ShoppingListDetailsViewModel(
                        savedStateHandle = savedStateHandle,
                        shoppingListDao = shoppingListDao,
                        shoppingListItemDao = shoppingListItemDao,
                        logParityToggled = application.container.logParityToggledUseCase,
                        userPreferencesRepository = application.container.userPreferencesRepository,
                    )
                }
            }
    }
}
