package com.br444n.unitwise.app.feature.scann

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.feature.scann.components.CameraPreviewView
import com.br444n.unitwise.app.feature.scann.components.ScannPermissionContent
import com.br444n.unitwise.app.feature.scann.components.ScannBottomSheet
import com.br444n.unitwise.app.feature.scann.components.ScannTopAppBar
import com.br444n.unitwise.app.feature.scann.components.ScannerOverlay
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import com.br444n.unitwise.R
import java.util.concurrent.Executors
import kotlinx.coroutines.delay

private const val PREVIEW_SELECTED_TEXT = "$24.50"
private val PREVIEW_DETECTED_TEXTS = listOf("500g", PREVIEW_SELECTED_TEXT, "1kg", "Brand Name")

private enum class CameraPermissionUiState {
    Requesting,
    Granted,
    Denied
}

@Suppress("unused")
@Composable
fun ScannScreen(
    onBackClick: () -> Unit,
    onUseSelectedClick: (String) -> Unit
) {
    val context = LocalContext.current
    var permissionState by rememberSaveable {
        mutableStateOf(
            if (context.hasCameraPermission()) {
                CameraPermissionUiState.Granted
            } else {
                CameraPermissionUiState.Requesting
            }
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = if (isGranted) {
            CameraPermissionUiState.Granted
        } else {
            CameraPermissionUiState.Denied
        }
    }

    LaunchedEffect(permissionState) {
        if (permissionState == CameraPermissionUiState.Requesting && !context.hasCameraPermission()) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    when (permissionState) {
        CameraPermissionUiState.Granted -> {
            GrantedScannRoute(
                onBackClick = onBackClick,
                onUseSelectedClick = onUseSelectedClick
            )
        }
        CameraPermissionUiState.Requesting -> {
            ScannPermissionContent(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.camera_permission_loading_title),
                description = stringResource(id = R.string.camera_permission_loading_description),
                actionLabel = null,
                onActionClick = null
            )
        }
        CameraPermissionUiState.Denied -> {
            ScannPermissionContent(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.camera_permission_denied_title),
                description = stringResource(id = R.string.camera_permission_denied_description),
                actionLabel = stringResource(id = R.string.camera_permission_retry),
                onActionClick = { permissionState = CameraPermissionUiState.Requesting }
            )
        }
    }
}

@Composable
private fun GrantedScannRoute(
    onBackClick: () -> Unit,
    onUseSelectedClick: (String) -> Unit
) {
    val viewModel: ScannViewModel = viewModel()
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
    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    var overlaySize by remember { mutableStateOf(IntSize.Zero) }
    var isAnalyzerEnabled by rememberSaveable { mutableStateOf(false) }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(analyzerExecutor) {
        onDispose { analyzerExecutor.shutdown() }
    }
    LaunchedEffect(previewSize, overlaySize) {
        isAnalyzerEnabled = false
        if (previewSize.width > 0 && previewSize.height > 0 && overlaySize.height > 0) {
            delay(350)
            isAnalyzerEnabled = true
        }
    }
    val imageAnalyzer: ImageAnalysis? = remember(previewSize, overlaySize) {
        if (previewSize.width == 0 || overlaySize.height == 0) return@remember null
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analyzer ->
                analyzer.setAnalyzer(
                    analyzerExecutor
                ) { imageProxy ->
                    onProcessImage(
                        imageProxy,
                        previewSize.width,
                        previewSize.height,
                        overlaySize.height
                    )
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera fills the whole screen behind the UI
        CameraPreviewView(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { previewSize = it },
            isFlashOn = state.isFlashOn,
            imageAnalyzer = imageAnalyzer.takeIf { isAnalyzerEnabled }
        )
        
        // UI Layer
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onSizeChanged { overlaySize = it }
            ) {
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

private fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
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
