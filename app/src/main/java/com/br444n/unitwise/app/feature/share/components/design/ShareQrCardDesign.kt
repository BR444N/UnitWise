package com.br444n.unitwise.app.feature.share.components.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.share.SharedComparisonLink
import com.br444n.unitwise.app.ui.theme.BadgeBackground
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.TextPrimary
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.options.square
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun ShareQrCardDesign(
    shareLink: SharedComparisonLink,
    modifier: Modifier = Modifier
) {
    val qrPainter = rememberQrCodePainter(
        data = shareLink.url,
        darkPixelShape = QrPixelShape.square(),
        darkBrush = QrBrush.solid(TextPrimary),
        frameBrush = QrBrush.solid(TextPrimary),
        ballBrush = QrBrush.solid(TextPrimary)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary,
            )

            Text(
                text = stringResource(id = R.string.share_preview_title),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = qrPainter,
                    contentDescription = stringResource(id = R.string.share_qr_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = BadgeBackground.copy(alpha = 0.14f)
            ) {
                Text(
                    text = shareLink.shareId,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareQrCardDesignPreview() {
    UnitWiseTheme {
        ShareQrCardDesign(
            shareLink = SharedComparisonLink(
                shareId = "ABX92K",
                encryptionKey = "preview",
                url = "https://unitwise-app.vercel.app/c/ABX92K#k=preview",
                shareText = ""
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
