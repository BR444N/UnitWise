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
    fun processImageProxy(imageProxy: ImageProxy, previewWidth: Int, previewHeight: Int, overlayHeight: Int) {
        if (!shouldProcessFrame(previewWidth, previewHeight, overlayHeight)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true
        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        
        getTextRecognizer().process(image)
            .addOnSuccessListener { visionText ->
                handleVisionTextResult(
                    visionText = visionText,
                    mediaWidth = mediaImage.width,
                    mediaHeight = mediaImage.height,
                    rotation = rotation,
                    previewWidth = previewWidth,
                    previewHeight = previewHeight,
                    overlayHeight = overlayHeight
                )
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }

    private fun shouldProcessFrame(previewWidth: Int, previewHeight: Int, overlayHeight: Int): Boolean {
        if (_uiState.value.selectedText != null || isProcessing || _uiState.value.detectedTexts.isNotEmpty()) return false
        if (previewWidth == 0 || previewHeight == 0 || overlayHeight == 0) return false
        return true
    }

    private fun handleVisionTextResult(
        visionText: com.google.mlkit.vision.text.Text,
        mediaWidth: Int,
        mediaHeight: Int,
        rotation: Int,
        previewWidth: Int,
        previewHeight: Int,
        overlayHeight: Int
    ) {
        val imageWidth = if (rotation == 90 || rotation == 270) mediaHeight else mediaWidth
        val imageHeight = if (rotation == 90 || rotation == 270) mediaWidth else mediaHeight

        val scale = maxOf(previewWidth.toFloat() / imageWidth, previewHeight.toFloat() / imageHeight)
        val offsetX = (imageWidth * scale - previewWidth) / 2f
        val offsetY = (imageHeight * scale - previewHeight) / 2f

        val targetWidth = previewWidth * 0.85f
        val targetHeight = targetWidth * 0.40f
        val targetLeft = (previewWidth - targetWidth) / 2f
        val targetTop = (overlayHeight - targetHeight) / 2f
        val targetRight = targetLeft + targetWidth
        val targetBottom = targetTop + targetHeight

        val validBlocks = visionText.textBlocks.filter { block ->
            val b = block.boundingBox ?: return@filter false
            val mappedCenterX = b.centerX().toFloat() * scale - offsetX
            val mappedCenterY = b.centerY().toFloat() * scale - offsetY
            mappedCenterX in targetLeft..targetRight && mappedCenterY in targetTop..targetBottom
        }.map { it.text.replace("\n", " ").trim() }

        if (validBlocks.isEmpty()) return

        val options = validBlocks.toMutableList()
        if (validBlocks.size > 1) {
            options.add(0, validBlocks.joinToString(" "))
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
