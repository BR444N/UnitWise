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
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.scann.bottomsheet.ScannBottomSheet
import com.br444n.unitwise.app.feature.scann.bottomsheet.ScannBottomSheetActions
import com.br444n.unitwise.app.feature.scann.components.CameraPreviewView
import com.br444n.unitwise.app.feature.scann.components.ScannPermissionContent
import com.br444n.unitwise.app.feature.scann.components.ScannTopAppBar
import com.br444n.unitwise.app.feature.scann.components.ScannerOverlay
import java.util.concurrent.Executors

private enum class CameraPermissionUiState {
    Requesting,
    Granted,
    Denied,
    PermanentlyDenied
}

private data class PreviewMetrics(
    val width: Int,
    val height: Int,
    val overlayHeight: Int
)

data class ScannContentActions(
    val onFlashClick: () -> Unit,
    val onBackClick: () -> Unit,
    val onStepChanged: (ScanStep) -> Unit,
    val onNameChanged: (String) -> Unit,
    val onContentChanged: (String) -> Unit,
    val onUnitChanged: (String) -> Unit,
    val onPriceChanged: (String) -> Unit,
    val onTextSelected: (String) -> Unit,
    val onConfirmClick: () -> Unit,
    val onScanAgainClick: () -> Unit,
    val onProcessImage: (ImageProxy, Int, Int, Int) -> Unit
)

@Composable
fun ScannScreen(
    onBackClick: () -> Unit,
    onResultClick: (ScannResult) -> Unit,
    inheritedUnit: String? = null
) {
    val context = LocalContext.current
    var permissionState by rememberSaveable {
        mutableStateOf(
            if (context.hasCameraPermission()) CameraPermissionUiState.Granted else CameraPermissionUiState.Requesting
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
        CameraPermissionUiState.Granted -> GrantedScannRoute(onBackClick, onResultClick, inheritedUnit)
        CameraPermissionUiState.Requesting -> ScannPermissionContent(
            onBackClick = onBackClick,
            title = stringResource(id = R.string.camera_permission_loading_title),
            description = stringResource(id = R.string.camera_permission_loading_description),
            actionLabel = null,
            onActionClick = null
        )
        CameraPermissionUiState.Denied -> ScannPermissionContent(
            onBackClick = onBackClick,
            title = stringResource(id = R.string.camera_permission_denied_title),
            description = stringResource(id = R.string.camera_permission_denied_description),
            actionLabel = stringResource(id = R.string.camera_permission_retry),
            onActionClick = { permissionState = CameraPermissionUiState.Requesting }
        )
        CameraPermissionUiState.PermanentlyDenied -> ScannPermissionContent(
            onBackClick = onBackClick,
            title = stringResource(id = R.string.camera_permission_denied_title),
            description = stringResource(id = R.string.camera_permission_permanently_denied_description),
            actionLabel = stringResource(id = R.string.camera_permission_open_settings),
            onActionClick = { context.openAppSettings() }
        )
    }
}

@Composable
private fun GrantedScannRoute(
    onBackClick: () -> Unit,
    onResultClick: (ScannResult) -> Unit,
    inheritedUnit: String?
) {
    val viewModel: ScannViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(inheritedUnit) {
        viewModel.setupInheritedUnit(inheritedUnit)
    }

    ScannContent(
        state = state,
        actions = ScannContentActions(
            onFlashClick = viewModel::toggleFlash,
            onBackClick = onBackClick,
            onStepChanged = viewModel::onStepChanged,
            onNameChanged = viewModel::onNameChanged,
            onContentChanged = viewModel::onContentChanged,
            onUnitChanged = viewModel::onUnitChanged,
            onPriceChanged = viewModel::onPriceChanged,
            onTextSelected = viewModel::selectText,
            onScanAgainClick = viewModel::scanAgain,
            onConfirmClick = { viewModel.buildResultOrNull()?.let(onResultClick) },
            onProcessImage = viewModel::processImageProxy
        )
    )
}

@Composable
fun ScannContent(
    state: ScannUiState,
    actions: ScannContentActions
) {
    val isCameraActive = state.currentStep != ScanStep.PRICE
    var previewWidth by remember { mutableIntStateOf(0) }
    var previewHeight by remember { mutableIntStateOf(0) }
    var overlayHeight by remember { mutableIntStateOf(0) }
    val overlayGuideText = guideTextForStep(state.currentStep)
    val previewMetrics = remember(previewWidth, previewHeight, overlayHeight) {
        PreviewMetrics(previewWidth, previewHeight, overlayHeight)
    }

    val imageAnalyzer = rememberImageAnalyzer(
        previewMetrics = previewMetrics,
        onProcessImage = actions.onProcessImage
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        ScannCameraLayer(
            isCameraActive = isCameraActive,
            isFlashOn = state.isFlashOn,
            imageAnalyzer = imageAnalyzer,
            onPreviewSizeChanged = {
                previewWidth = it.width
                previewHeight = it.height
            }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).onSizeChanged { overlayHeight = it.height }) {
                if (isCameraActive) {
                    ScannerOverlay(
                        guideText = overlayGuideText,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ScannTopAppBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isFlashOn = state.isFlashOn,
                    onBackClick = actions.onBackClick,
                    onFlashClick = actions.onFlashClick
                )
            }
        }

        ScannBottomSheet(
            state = state,
            actions = ScannBottomSheetActions(
                onStepChanged = actions.onStepChanged,
                onNameChanged = actions.onNameChanged,
                onContentChanged = actions.onContentChanged,
                onUnitChanged = actions.onUnitChanged,
                onPriceChanged = actions.onPriceChanged,
                onScanAgainClick = actions.onScanAgainClick,
                onConfirmClick = actions.onConfirmClick
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    AutoSelectDetectedText(state = state, onTextSelected = actions.onTextSelected)
}

@Composable
private fun AutoSelectDetectedText(
    state: ScannUiState,
    onTextSelected: (String) -> Unit
) {
    LaunchedEffect(state.detectedTexts) {
        if (state.detectedTexts.isNotEmpty() && state.selectedText == null) {
            onTextSelected(state.detectedTexts.first())
        }
    }
}

@Composable
private fun rememberImageAnalyzer(
    previewMetrics: PreviewMetrics,
    onProcessImage: (ImageProxy, Int, Int, Int) -> Unit
): ImageAnalysis {
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(analyzerExecutor) {
        onDispose { analyzerExecutor.shutdown() }
    }

    val analyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    DisposableEffect(analyzer, analyzerExecutor, previewMetrics, onProcessImage) {
        analyzer.setAnalyzer(analyzerExecutor) { imageProxy ->
            if (
                previewMetrics.width > 0 &&
                previewMetrics.height > 0 &&
                previewMetrics.overlayHeight > 0
            ) {
                onProcessImage(
                    imageProxy,
                    previewMetrics.width,
                    previewMetrics.height,
                    previewMetrics.overlayHeight
                )
            } else {
                imageProxy.close()
            }
        }
        onDispose { analyzer.clearAnalyzer() }
    }

    return analyzer
}

@Composable
private fun ScannCameraLayer(
    isCameraActive: Boolean,
    isFlashOn: Boolean,
    imageAnalyzer: ImageAnalysis,
    onPreviewSizeChanged: (androidx.compose.ui.unit.IntSize) -> Unit
) {
    if (isCameraActive) {
        CameraPreviewView(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged(onPreviewSizeChanged),
            isFlashOn = isFlashOn,
            imageAnalyzer = imageAnalyzer
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
    }
}

@Composable
private fun guideTextForStep(step: ScanStep): String {
    return when (step) {
        ScanStep.NAME -> stringResource(id = R.string.scann_step_name_guide)
        ScanStep.CONTENT -> stringResource(id = R.string.scann_step_content_guide)
        ScanStep.PRICE -> stringResource(id = R.string.scann_step_price_guide)
    }
}

private fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
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
