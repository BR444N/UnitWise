package com.br444n.unitwise.app.feature.scann

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.feature.scann.components.CameraPreviewView
import com.br444n.unitwise.app.feature.scann.components.ScannBottomSheet
import com.br444n.unitwise.app.feature.scann.components.ScannTopAppBar
import com.br444n.unitwise.app.feature.scann.components.ScannerOverlay
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember

private const val PREVIEW_SELECTED_TEXT = "$24.50"
private val PREVIEW_DETECTED_TEXTS = listOf("500g", PREVIEW_SELECTED_TEXT, "1kg", "Brand Name")

@Suppress("unused")
@Composable
fun ScannScreen(
    onBackClick: () -> Unit,
    onUseSelectedClick: (String) -> Unit,
    viewModel: ScannViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ScannContent(
        state = state,
        onFlashClick = viewModel::toggleFlash,
        onBackClick = onBackClick,
        onTextSelected = viewModel::selectText,
        onUseSelectedClick = { state.selectedText?.let { onUseSelectedClick(it) } },
        onScanAgainClick = viewModel::scanAgain,
        onProcessImage = viewModel::processImageProxy
    )
}

@Composable
fun ScannContent(
    state: ScannUiState,
    onFlashClick: () -> Unit,
    onBackClick: () -> Unit,
    onTextSelected: (String) -> Unit,
    onUseSelectedClick: () -> Unit,
    onScanAgainClick: () -> Unit,
    onProcessImage: (ImageProxy, Int, Int, Int) -> Unit
) {
    var previewWidth by remember { mutableIntStateOf(0) }
    var previewHeight by remember { mutableIntStateOf(0) }
    var overlayHeight by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val imageAnalyzer = remember(previewWidth, previewHeight, overlayHeight) {
        if (previewWidth == 0 || overlayHeight == 0) return@remember null
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analyzer ->
                analyzer.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy -> onProcessImage(imageProxy, previewWidth, previewHeight, overlayHeight) }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera fills the whole screen behind the UI
        CameraPreviewView(
            modifier = Modifier.fillMaxSize().onSizeChanged {
                previewWidth = it.width
                previewHeight = it.height
            },
            imageAnalyzer = imageAnalyzer
        )
        
        // UI Layer
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f).onSizeChanged {
                overlayHeight = it.height
            }) {
                // Scanner mask perfectly centered in the remaining available upper space
                ScannerOverlay(
                    modifier = Modifier.fillMaxSize()
                )
                
                ScannTopAppBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isFlashOn = state.isFlashOn,
                    onBackClick = onBackClick,
                    onFlashClick = onFlashClick
                )
            }

            // Bottom sheet sitting at the bottom of the column
            ScannBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                detectedTexts = state.detectedTexts,
                selectedText = state.selectedText,
                onTextSelected = onTextSelected,
                onUseSelectedClick = onUseSelectedClick,
                onScanAgainClick = onScanAgainClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannContentPreview() {
    UnitWiseTheme {
        ScannContent(
            state = ScannUiState(
                isFlashOn = false,
                detectedTexts = PREVIEW_DETECTED_TEXTS,
                selectedText = PREVIEW_SELECTED_TEXT
            ),
            onFlashClick = {},
            onBackClick = {},
            onTextSelected = {},
            onUseSelectedClick = {},
            onScanAgainClick = {},
            onProcessImage = { _, _, _, _ -> }
        )
    }
}
