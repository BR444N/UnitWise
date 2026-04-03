package com.br444n.unitwise.app.feature.share

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ShareImageExporter {
    private const val TAG = "ShareImageExporter"

    suspend fun export(
        context: Context,
        bitmap: Bitmap,
        shareId: String
    ): Uri? {
        return runCatching {
            withContext(ShareDispatchers.io) {
                writeBitmapToCache(
                    context = context,
                    bitmap = bitmap,
                    shareId = shareId
                )
            }
        }.getOrElse { throwable ->
            Log.e(TAG, "Failed to export shared comparison image", throwable)
            null
        }
    }

    private fun writeBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        shareId: String
    ): Uri {
        val shareDirectory = File(context.cacheDir, "shared-comparisons").apply {
            mkdirs()
        }
        val shareFile = File(shareDirectory, "unitwise-$shareId.png")
        FileOutputStream(shareFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )
    }
}
