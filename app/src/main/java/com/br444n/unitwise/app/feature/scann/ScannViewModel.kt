package com.br444n.unitwise.app.feature.scann

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScannViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScannUiState())
    val uiState: StateFlow<ScannUiState> = _uiState.asStateFlow()

    private var textRecognizer: TextRecognizer? = null
    private var isProcessing = false

    fun toggleFlash() {
        _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
    }

    fun selectText(text: String) {
        _uiState.update { it.copy(selectedText = text) }
    }

    fun scanAgain() {
        _uiState.update { it.copy(selectedText = null, detectedTexts = emptyList()) }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun processImageProxy(
        imageProxy: ImageProxy,
        previewWidth: Int,
        previewHeight: Int,
        overlayHeight: Int
    ) {
        if (!shouldProcessFrame(previewWidth, previewHeight, overlayHeight)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        try {
            isProcessing = true
            val rotation = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotation)

            getTextRecognizer().process(image)
                .addOnSuccessListener { visionText ->
                    handleVisionTextResult(
                        visionText = visionText,
                        config = OverlayFrameConfig(
                            mediaWidth = mediaImage.width,
                            mediaHeight = mediaImage.height,
                            rotation = rotation,
                            previewWidth = previewWidth,
                            previewHeight = previewHeight,
                            overlayHeight = overlayHeight
                        )
                    )
                }
                .addOnFailureListener {
                    isProcessing = false
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
        if (_uiState.value.selectedText != null || isProcessing || _uiState.value.detectedTexts.isNotEmpty()) return false
        if (previewWidth <= 0 || previewHeight <= 0 || overlayHeight <= 0) return false
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

        val detectedOptions = overlayOptions.takeIf { it.isNotEmpty() }
            ?: visionText.textBlocks
            .flatMap { block -> block.lines.map { it.text.replace("\n", " ").trim() } }
            .filter { it.isNotBlank() }
            .takeIf { it.isNotEmpty() }
            ?: visionText.textBlocks
                .map { it.text.replace("\n", " ").trim() }
                .filter { it.isNotBlank() }
                .takeIf { it.isNotEmpty() }
            ?: visionText.text
                .split('\n')
                .map { it.trim() }
                .filter { it.isNotBlank() }

        if (detectedOptions.isEmpty()) return

        val options = detectedOptions.toMutableList()
        if (detectedOptions.size > 1) {
            options.add(0, detectedOptions.joinToString(" "))
        }
        
        _uiState.update { it.copy(detectedTexts = options.distinct()) }
    }

    private fun getTextRecognizer(): TextRecognizer {
        return textRecognizer ?: TextRecognition.getClient(
            TextRecognizerOptions.DEFAULT_OPTIONS
        ).also { textRecognizer = it }
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer?.close()
        textRecognizer = null
    }
}
