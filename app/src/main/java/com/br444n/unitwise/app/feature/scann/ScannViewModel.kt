package com.br444n.unitwise.app.feature.scann

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.br444n.unitwise.app.domain.model.MeasurementUnit
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.Executors

class ScannViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScannUiState())
    val uiState: StateFlow<ScannUiState> = _uiState.asStateFlow()

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )
    private val mlExecutor = Executors.newSingleThreadExecutor()
    @Volatile private var isProcessing = false
    private var lastProcessTime = 0L

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    fun setupInheritedUnit(unit: String?) {
        if (unit.isNullOrBlank()) return
        _uiState.update { state ->
            if (state.inheritedUnit != null) return@update state
            val normalized = if (unit.equals("l", ignoreCase = true)) "L" else unit.lowercase()
            val compatible = MeasurementUnit.compatibleUnitsFor(normalized)
            state.copy(
                inheritedUnit = normalized,
                selectedUnit = if (state.selectedUnit in compatible) state.selectedUnit else compatible.first()
            )
        }
    }

    fun onStepChanged(step: ScanStep) {
        _uiState.update { state ->
            if (state.currentStep == step) return@update state
            state.copy(
                currentStep = step,
                selectedText = null,
                detectedTexts = emptyList()
            )
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(productName = value) }
    }

    fun onContentChanged(value: String) {
        _uiState.update { it.copy(content = value) }
    }

    fun onUnitChanged(value: String) {
        _uiState.update { state ->
            if (value !in state.compatibleUnits) return@update state
            state.copy(selectedUnit = value)
        }
    }

    fun onPriceChanged(value: String) {
        _uiState.update { it.copy(price = value) }
    }

    fun selectText(text: String) {
        _uiState.update { it.copy(selectedText = text) }
        applyDetectedTextToCurrentStep(text)
    }

    fun scanAgain() {
        _uiState.update { state ->
            when (state.currentStep) {
                ScanStep.NAME -> state.copy(
                    productName = "",
                    selectedText = null,
                    detectedTexts = emptyList()
                )
                ScanStep.CONTENT -> state.copy(
                    content = "",
                    selectedUnit = defaultUnitForContentRescan(state),
                    selectedText = null,
                    detectedTexts = emptyList()
                )
                ScanStep.PRICE -> state.copy(
                    selectedText = null,
                    detectedTexts = emptyList()
                )
            }
        }
    }

    fun buildResultOrNull(): ScannResult? {
        val state = _uiState.value
        if (!state.isDataReady) return null
        return ScannResult(
            productName = state.productName.trim(),
            content = state.content.trim(),
            selectedUnit = state.selectedUnit.trim(),
            price = state.price.trim()
        )
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun processImageProxy(
        imageProxy: ImageProxy,
        previewWidth: Int,
        previewHeight: Int,
        overlayHeight: Int
    ) {
        if (mlExecutor.isShutdown || !shouldProcessFrame(previewWidth, previewHeight, overlayHeight)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true
        lastProcessTime = System.currentTimeMillis()
        try {
            val rotation = imageProxy.imageInfo.rotationDegrees
            val mediaWidth = mediaImage.width
            val mediaHeight = mediaImage.height
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (!mlExecutor.isShutdown) {
                        try {
                            mlExecutor.execute {
                                handleVisionTextResult(
                                    visionText = visionText,
                                    config = OverlayFrameConfig(
                                        mediaWidth = mediaWidth,
                                        mediaHeight = mediaHeight,
                                        rotation = rotation,
                                        previewWidth = previewWidth,
                                        previewHeight = previewHeight,
                                        overlayHeight = overlayHeight
                                    )
                                )
                            }
                        } catch (_: java.util.concurrent.RejectedExecutionException) {
                            // Executor was closed while ML callback arrived; frame lifecycle is
                            // already handled in addOnCompleteListener(imageProxy.close()).
                        }
                    }
                }
                .addOnCompleteListener {
                    isProcessing = false
                    imageProxy.close()
                }
        } catch (_: Exception) {
            isProcessing = false
            imageProxy.close()
        }
    }

    private fun shouldProcessFrame(previewWidth: Int, previewHeight: Int, overlayHeight: Int): Boolean {
        val state = _uiState.value
        if (isProcessing) return false
        if (state.currentStep == ScanStep.PRICE) return false
        if (System.currentTimeMillis() - lastProcessTime < 500) return false
        if (previewWidth <= 0 || previewHeight <= 0 || overlayHeight <= 0) return false
        if (state.selectedText != null || state.detectedTexts.isNotEmpty()) return false
        return true
    }

    private fun handleVisionTextResult(
        visionText: com.google.mlkit.vision.text.Text,
        config: OverlayFrameConfig
    ) {
        val overlayOptions = ScannOverlayTextFilter.filterToOverlay(
            blocks = visionText.textBlocks.mapNotNull { block ->
                val bounds = block.boundingBox ?: return@mapNotNull null
                OverlayTextBlock(
                    text = block.text.replace("\n", " ").trim(),
                    centerX = bounds.centerX().toFloat(),
                    centerY = bounds.centerY().toFloat()
                )
            },
            config = config
        )

        val options = filterDetectedByCurrentStep(overlayOptions).distinct()
        if (options == _uiState.value.detectedTexts) return
        _uiState.update { it.copy(detectedTexts = options) }
    }

    private fun applyDetectedTextToCurrentStep(text: String) {
        val state = _uiState.value
        when (state.currentStep) {
            ScanStep.NAME -> {
                if (text.any(Char::isLetter)) {
                    _uiState.update { it.copy(productName = text.trim()) }
                }
            }
            ScanStep.CONTENT -> {
                val match = CONTENT_PATTERN.find(text.lowercase()) ?: return
                val number = match.groupValues[1].replace(',', '.')
                val unitRaw = match.groupValues[2]
                val normalizedUnit = normalizeUnit(unitRaw) ?: return
                if (normalizedUnit !in state.compatibleUnits) return

                _uiState.update {
                    it.copy(
                        content = number,
                        selectedUnit = normalizedUnit
                    )
                }
            }
            ScanStep.PRICE -> Unit
        }
    }

    private fun filterDetectedByCurrentStep(items: List<String>): List<String> {
        return when (_uiState.value.currentStep) {
            ScanStep.NAME -> items.filter { value -> value.any(Char::isLetter) }
            ScanStep.CONTENT -> items.filter { CONTENT_PATTERN.containsMatchIn(it.lowercase()) }
            ScanStep.PRICE -> emptyList()
        }
    }

    private fun normalizeUnit(unit: String): String? {
        val normalized = unit.lowercase().replace(".", "")
        return when (normalized) {
            "g", "gr", "gram", "grams" -> "g"
            "kg", "kilo", "kilos" -> "kg"
            "ml", "mi", "m1" -> "ml"
            "l", "lt", "lts", "litro", "litros" -> "L"
            "pz", "pza", "pzas", "pcs", "pc" -> "pcs"
            else -> null
        }
    }

    private fun defaultUnitForContentRescan(state: ScannUiState): String {
        val inherited = state.inheritedUnit ?: return state.selectedUnit
        val normalizedInherited = normalizeUnit(inherited) ?: inherited
        val compatible = MeasurementUnit.compatibleUnitsFor(normalizedInherited)
        if (normalizedInherited in compatible) return normalizedInherited
        return compatible.firstOrNull() ?: state.selectedUnit
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
        mlExecutor.shutdown()
    }

    companion object {
        // Uses app-supported units plus supermarket label aliases.
        private val CONTENT_PATTERN = Regex(
            // Keep this broad and let normalizeUnit(...) validate/standardize aliases.
            pattern = """(\d+(?:[.,]\d+)?)\s*([a-z0-9.]{1,6})\b""",
            option = RegexOption.IGNORE_CASE
        )
    }
}
