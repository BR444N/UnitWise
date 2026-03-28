package com.br444n.unitwise.app.feature.scann.components

import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean = false,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    imageAnalyzer: UseCase? = null
) {
    if (LocalInspectionMode.current) {
        PreviewPlaceholder(modifier)
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context.applicationContext) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }.also { previewView = it }
            },
            update = { createdPreviewView ->
                previewView = createdPreviewView
            }
        )
    }

    DisposableEffect(
        context,
        lifecycleOwner,
        cameraSelector,
        imageAnalyzer,
        previewView
    ) {
        val currentPreviewView = previewView
        if (currentPreviewView == null) {
            onDispose { }
        } else {
            val executor = ContextCompat.getMainExecutor(context)
            val listener = Runnable {
                boundCamera = bindCameraUseCases(
                    cameraProviderFuture = cameraProviderFuture,
                    lifecycleOwner = lifecycleOwner,
                    cameraSelector = cameraSelector,
                    previewView = currentPreviewView,
                    imageAnalyzer = imageAnalyzer,
                    isFlashOn = isFlashOn
                )
            }

            cameraProviderFuture.addListener(listener, executor)

            onDispose {
                runCatching {
                    if (cameraProviderFuture.isDone) {
                        cameraProviderFuture.get().unbindAll()
                    }
                }
                boundCamera = null
            }
        }
    }

    DisposableEffect(boundCamera, isFlashOn) {
        boundCamera?.cameraControl?.enableTorch(isFlashOn)
        onDispose { }
    }
}

@Composable
private fun PreviewPlaceholder(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    )
}

private fun bindCameraUseCases(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    imageAnalyzer: UseCase?,
    isFlashOn: Boolean
): Camera? {
    return try {
        val cameraProvider = cameraProviderFuture.get()

        if (!cameraProvider.hasCamera(cameraSelector)) {
            cameraProvider.unbindAll()
            return null
        }

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val useCases = buildList {
            add(preview)
            imageAnalyzer?.let(::add)
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            *useCases.toTypedArray()
        ).also { camera ->
            camera.cameraControl.enableTorch(isFlashOn)
        }
    } catch (exc: Exception) {
        exc.printStackTrace()
        null
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun CameraPreviewViewPreview() {
    UnitWiseTheme {
        CameraPreviewView(modifier = Modifier.fillMaxSize())
    }
}
