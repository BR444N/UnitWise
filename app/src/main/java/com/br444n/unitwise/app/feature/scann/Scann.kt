package com.br444n.unitwise.app.feature.scann

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

data class ScannUiState(
    val isFlashOn: Boolean = false,
    val detectedTexts: List<String> = emptyList(),
    val selectedText: String? = null
)

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
        onScanAgainClick = viewModel::scanAgain
    )
}

@Composable
fun ScannContent(
    state: ScannUiState,
    onFlashClick: () -> Unit,
    onBackClick: () -> Unit,
    onTextSelected: (String) -> Unit,
    onUseSelectedClick: () -> Unit,
    onScanAgainClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera fills the whole screen behind the UI
        CameraPreviewView(
            modifier = Modifier.fillMaxSize()
        )
        
        // UI Layer
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f)) {
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
            onScanAgainClick = {}
        )
    }
}
