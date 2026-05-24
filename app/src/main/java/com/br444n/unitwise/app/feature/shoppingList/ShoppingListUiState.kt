package com.br444n.unitwise.app.feature.shoppingList

import com.br444n.unitwise.app.data.local.dao.ShoppingListWithItemCount

data class ShoppingListUiState(
    val isLoading: Boolean = true,
    val lists: List<ShoppingListWithItemCount> = emptyList(),
    val error: String? = null
)
