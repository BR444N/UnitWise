package com.br444n.unitwise.app.feature.scann

import com.br444n.unitwise.app.domain.model.MeasurementUnit
import java.io.Serializable

enum class ScanStep(val number: Int, val progress: Float) {
    NAME(number = 1, progress = 0.33f),
    CONTENT(number = 2, progress = 0.66f),
    PRICE(number = 3, progress = 1f)
}

data class ScannResult(
    val productName: String,
    val content: String,
    val selectedUnit: String,
    val price: String
) : Serializable

data class ScannUiState(
    val isFlashOn: Boolean = false,
    val currentStep: ScanStep = ScanStep.NAME,
    val detectedTexts: List<String> = emptyList(),
    val selectedText: String? = null,
    val productName: String = "",
    val content: String = "",
    val selectedUnit: String = "g",
    val price: String = "",
    val inheritedUnit: String? = null
) {
    val compatibleUnits: List<String>
        get() = MeasurementUnit.compatibleUnitsFor(inheritedUnit ?: selectedUnit)

    val isNameValid: Boolean
        get() = productName.isNotBlank()

    val isContentValid: Boolean
        get() = content.toDoubleOrNull()?.let { it > 0 } == true && selectedUnit.isNotBlank()

    val isPriceValid: Boolean
        get() = price.toDoubleOrNull()?.let { it > 0 } == true

    val isDataReady: Boolean
        get() = isNameValid && isContentValid && isPriceValid
}
