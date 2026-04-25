package com.br444n.unitwise.app.feature.scann

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.core.app.ActivityCompat
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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

private const val PREVIEW_SELECTED_TEXT = "$24.50"
private val PREVIEW_DETECTED_TEXTS = listOf("500g", PREVIEW_SELECTED_TEXT, "1kg", "Brand Name")

private enum class CameraPermissionUiState {
    Requesting,
    Granted,
    Denied,
    PermanentlyDenied
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
        permissionState = when {
            isGranted -> CameraPermissionUiState.Granted
            ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(),
                Manifest.permission.CAMERA
            ) -> CameraPermissionUiState.Denied
            else -> CameraPermissionUiState.PermanentlyDenied
        }
    }

    LaunchedEffect(permissionState) {
        if (permissionState == CameraPermissionUiState.Requesting && !context.hasCameraPermission()) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // When the user returns from Settings after granting the permission,
    // ON_RESUME fires, we check immediately — no manual retry needed.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME &&
                permissionState == CameraPermissionUiState.PermanentlyDenied &&
                context.hasCameraPermission()
            ) {
                permissionState = CameraPermissionUiState.Granted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
        CameraPermissionUiState.PermanentlyDenied -> {
            ScannPermissionContent(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.camera_permission_denied_title),
                description = stringResource(id = R.string.camera_permission_permanently_denied_description),
                actionLabel = stringResource(id = R.string.camera_permission_open_settings),
                onActionClick = { context.openAppSettings() }
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
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(analyzerExecutor) {
        onDispose { analyzerExecutor.shutdown() }
    }
    val imageAnalyzer: ImageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analyzer ->
                analyzer.setAnalyzer(analyzerExecutor) { imageProxy ->
                    if (previewSize.width > 0 && overlaySize.height > 0) {
                        onProcessImage(
                            imageProxy,
                            previewSize.width,
                            previewSize.height,
                            overlaySize.height
                        )
                    } else {
                        imageProxy.close()
                    }
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
            imageAnalyzer = imageAnalyzer
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

private fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    error("No Activity found in context hierarchy")
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    startActivity(intent)
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
