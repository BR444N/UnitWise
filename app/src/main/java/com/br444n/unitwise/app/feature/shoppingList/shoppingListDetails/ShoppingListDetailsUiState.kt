package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails

import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity

data class ShoppingListDetailsUiState(
    val isLoading: Boolean = true,
    val listName: String = "",
    val items: List<ShoppingListItemEntity> = emptyList(),
    val error: String? = null,
    val fairTotalA: Double = 0.0,
    val fairTotalB: Double = 0.0,
    val totalWithOrphansA: Double = 0.0,
    val totalWithOrphansB: Double = 0.0,
    val smartTotal: Double = 0.0,
    val hasOrphans: Boolean = false,
)
