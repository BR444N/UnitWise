package com.br444n.unitwise.app.feature.history

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity

data class HistoryItemUiModel(
    val entity: ComparisonEntity,
    val winnerName: String?
)

data class HistoryUiState(
    val comparisons: List<HistoryItemUiModel> = emptyList(),
    val isLoading: Boolean = true
)