package com.br444n.unitwise.app.feature.scann.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ScannerOverlay(
    guideText: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            // OCR Text Window shape (Wide rectangle)
            val windowWidth = canvasWidth * 0.85f
            val windowHeight = windowWidth * 0.40f
            val left = (canvasWidth - windowWidth) / 2f
            val top = (canvasHeight - windowHeight) / 2f

            val rect = Rect(left, top, left + windowWidth, top + windowHeight)
            val roundRect = RoundRect(rect, cornerRadius = CornerRadius(16.dp.toPx()))
            
            val screenPath = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            }
            val windowPath = Path().apply {
                addRoundRect(roundRect)
            }
            
            // Overlay with EvenOdd to magically create a cutout
            val dimPath = Path().apply {
                addPath(screenPath)
                addPath(windowPath)
                fillType = PathFillType.EvenOdd
            }
            
            // Draw dimmed screen
            drawPath(
                path = dimPath,
                color = Color.Black.copy(alpha = 0.6f)
            )

            // Draw "crop free" style corners
            val cornerLength = 32.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val radius = 16.dp.toPx()
            
            // Top-Left corner
            drawPath(
                path = Path().apply {
                    moveTo(left, top + cornerLength)
                    lineTo(left, top + radius)
                    quadraticTo(left, top, left + radius, top)
                    lineTo(left + cornerLength, top)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth)
            )
            // Top-Right corner
            drawPath(
                path = Path().apply {
                    moveTo(left + windowWidth - cornerLength, top)
                    lineTo(left + windowWidth - radius, top)
                    quadraticTo(left + windowWidth, top, left + windowWidth, top + radius)
                    lineTo(left + windowWidth, top + cornerLength)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth)
            )
            // Bottom-Left corner
            drawPath(
                path = Path().apply {
                    moveTo(left, top + windowHeight - cornerLength)
                    lineTo(left, top + windowHeight - radius)
                    quadraticTo(left, top + windowHeight, left + radius, top + windowHeight)
                    lineTo(left + cornerLength, top + windowHeight)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth)
            )
            // Bottom-Right corner
            drawPath(
                path = Path().apply {
                    moveTo(left + windowWidth, top + windowHeight - cornerLength)
                    lineTo(left + windowWidth, top + windowHeight - radius)
                    quadraticTo(left + windowWidth, top + windowHeight, left + windowWidth - radius, top + windowHeight)
                    lineTo(left + windowWidth - cornerLength, top + windowHeight)
                },
                color = primaryColor,
                style = Stroke(width = strokeWidth)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = guideText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF34C759),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Invisible placeholder mimicking the exact size of the crop window
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f / 0.40f) // width / height ratio
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF555555)
@Composable
fun ScannerOverlayPreview() {
    UnitWiseTheme {
        ScannerOverlay(guideText = stringResource(id = R.string.scann_step_name_guide))
    }
}
