package com.br444n.unitwise.app.feature.share

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.br444n.unitwise.R

fun launchNativeShareSheet(
    context: Context,
    shareText: String,
    imageUri: Uri
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_STREAM, imageUri)
        clipData = ClipData.newUri(context.contentResolver, "shared comparison", imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooserIntent = Intent.createChooser(
        sendIntent,
        context.getString(R.string.share_sheet_chooser_title)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    context.startActivity(chooserIntent)
}
