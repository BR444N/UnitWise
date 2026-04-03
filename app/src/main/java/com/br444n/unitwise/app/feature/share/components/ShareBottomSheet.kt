package com.br444n.unitwise.app.feature.share.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.feature.share.ShareQrCard
import com.br444n.unitwise.app.feature.share.ShareImageExporter
import com.br444n.unitwise.app.feature.share.SharedComparisonLink
import com.br444n.unitwise.app.feature.share.launchNativeShareSheet
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.components.UnitWiseLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonShareBottomSheet(
    comparison: ComparisonEntity,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val repository = remember(context) {
        (context.applicationContext as UnitWiseApplication).container.comparisonRepository
    }
    val shareCardGraphicsLayer = rememberGraphicsLayer()
    var publishFailed by remember { mutableStateOf(false) }
    var exportFailed by remember { mutableStateOf(false) }

    val shareLink by produceState<SharedComparisonLink?>(initialValue = null, comparison.id) {
        value = runCatching {
            repository.publishSharedComparison(comparison)
        }.getOrNull()
        publishFailed = value == null
    }

    val shareUri by produceState<Uri?>(initialValue = null, shareLink?.url) {
        val currentShareLink = shareLink ?: run {
            exportFailed = false
            value = null
            return@produceState
        }
        withFrameNanos { }
        withFrameNanos { }

        val bitmap = runCatching {
            shareCardGraphicsLayer.toImageBitmap().asAndroidBitmap()
        }.getOrNull()

        value = bitmap?.let {
            ShareImageExporter.export(context, it, currentShareLink.shareId)
        }
        exportFailed = value == null
    }

    LaunchedEffect(shareUri) {
        val readyUri = shareUri ?: return@LaunchedEffect
        val currentShareLink = shareLink ?: return@LaunchedEffect
        launchNativeShareSheet(
            context = context,
            shareText = currentShareLink.shareText,
            imageUri = readyUri
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            ShareBottomSheetContent(
                modifier = Modifier.drawWithContent {
                    shareCardGraphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(shareCardGraphicsLayer)
                },
                shareLink = shareLink,
                isReady = shareUri != null,
                hasError = publishFailed || exportFailed,
                onShareAgainClick = {
                    val currentShareLink = shareLink
                    if (currentShareLink != null) {
                        shareUri?.let { uri ->
                        launchNativeShareSheet(
                            context = context,
                            shareText = currentShareLink.shareText,
                            imageUri = uri
                        )
                    }
                    }
                }
            )

            if ((shareLink == null && !publishFailed) || (shareLink != null && shareUri == null && !exportFailed)) {
                UnitWiseLoading(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(24.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ShareBottomSheetContent(
    modifier: Modifier = Modifier,
    shareLink: SharedComparisonLink?,
    isReady: Boolean,
    hasError: Boolean,
    onShareAgainClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(width = 40.dp, height = 4.dp),
            shape = RoundedCornerShape(2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.share_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(id = R.string.share_sheet_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        shareLink?.let {
            ShareQrCard(
                shareLink = it,
                modifier = modifier
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = shareLink?.url.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Badge)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.share_qr_expiration_message),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (hasError) {
            Text(
                text = stringResource(id = R.string.share_sheet_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        } else if (isReady) {
            Button(
                onClick = onShareAgainClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.share_again))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
