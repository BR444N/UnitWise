package com.br444n.unitwise.app.permission

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberCameraPermissionHandler(
    onPermissionGranted: (String) -> Unit
): (String) -> Unit {
    val context = LocalContext.current
    var pendingTarget by remember { mutableStateOf<String?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingTarget?.let { onPermissionGranted(it) }
        }
    }

    return { target ->
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(target)
        } else {
            pendingTarget = target
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }
}
