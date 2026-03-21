package com.br444n.unitwise.app.feature.scann.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScannBottomSheet(
    modifier: Modifier = Modifier,
    detectedTexts: List<String>,
    selectedText: String?,
    onTextSelected: (String) -> Unit,
    onUseSelectedClick: () -> Unit,
    onScanAgainClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Un pequeño indicador visual de bottom sheet (opcional, decorativo)
            Surface(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp),
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            ) {}

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.detected_text_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.detected_text_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Flujo de badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                detectedTexts.forEach { text ->
                    DetectedTextBadge(
                        text = text,
                        isSelected = text == selectedText,
                        onClick = { onTextSelected(text) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ScannButtonSelectedText(
                enabled = selectedText != null,
                onClick = onUseSelectedClick
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ScannButtonScanAgain(
                onClick = onScanAgainClick
            )
            
            // Espacio extra para navegación fluida
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF555555)
@Composable
fun ScannBottomSheetPreview() {
    UnitWiseTheme {
        ScannBottomSheet(
            detectedTexts = listOf("500g", "$24.50", "1kg", "Brand Name"),
            selectedText = "$24.50",
            onTextSelected = {},
            onUseSelectedClick = {},
            onScanAgainClick = {}
        )
    }
}
