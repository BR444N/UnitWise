package com.br444n.unitwise.app.feature.history

import com.br444n.unitwise.app.data.local.entity.ComparisonEntity

data class HistoryUiState(
    val comparisons: List<ComparisonEntity> = emptyList(),
    val isLoading: Boolean = true
)