package com.br444n.unitwise.app.feature.scann

data class ScannUiState(
    val isFlashOn: Boolean = false,
    val detectedTexts: List<String> = emptyList(),
    val selectedText: String? = null
)
