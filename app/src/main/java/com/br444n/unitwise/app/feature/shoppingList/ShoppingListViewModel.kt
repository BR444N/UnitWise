package com.br444n.unitwise.app.feature.shoppingList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.data.local.dao.ShoppingListDao
import com.br444n.unitwise.app.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ShoppingListViewModel(
    private val shoppingListDao: ShoppingListDao
) : ViewModel() {

    val uiState: StateFlow<ShoppingListUiState> = shoppingListDao.getAllListsWithCount()
        .map { lists -> ShoppingListUiState(isLoading = false, lists = lists) }
        .catch { e -> emit(ShoppingListUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ShoppingListUiState(isLoading = true)
        )

    fun createList(name: String, colorArgb: Int, onListCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val newList = ShoppingListEntity(
                name = name,
                colorBadge = colorArgb,
                timestamp = System.currentTimeMillis()
            )
            val id = shoppingListDao.insertList(newList)
            onListCreated(id.toInt())
        }
    }

    fun deleteLists(ids: Set<Int>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            shoppingListDao.deleteLists(ids)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                val shoppingListDao = application.container.shoppingListDao
                ShoppingListViewModel(shoppingListDao = shoppingListDao)
            }
        }
    }
}
