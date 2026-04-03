package com.br444n.unitwise.app.feature.share

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.br444n.unitwise.app.feature.share.components.design.ShareQrCardDesign

@Composable
fun ShareQrCard(
    shareLink: SharedComparisonLink,
    modifier: Modifier = Modifier
) {
    ShareQrCardDesign(
        shareLink = shareLink,
        modifier = modifier
    )
}
