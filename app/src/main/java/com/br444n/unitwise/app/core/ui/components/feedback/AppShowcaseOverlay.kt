package com.br444n.unitwise.app.core.ui.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.DarkBackgroundMain
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.joco.compose_showcaseview.ShowcaseAlignment
import com.joco.compose_showcaseview.ShowcasePosition
import com.joco.compose_showcaseview.ShowcaseView
import com.joco.compose_showcaseview.highlight.ShowcaseHighlight

data class AppShowcaseConfig(
    val titleRes: Int,
    val bodyRes: Int,
    val actionRes: Int,
    val dialogAlignment: Alignment = Alignment.Center,
    val topPadding: Dp = 0.dp,
    val bottomPadding: Dp = 0.dp,
)

@Composable
fun AppShowcaseOverlay(
    targetCoordinates: LayoutCoordinates?,
    config: AppShowcaseConfig,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (targetCoordinates == null || !targetCoordinates.isAttached) return

    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackgroundMain
    val nextButtonColor = if (isDarkTheme) BrandPrimary else DarkBackgroundMain
    val nextButtonContentColor = if (isDarkTheme) DarkBackgroundMain else Color.White

    ShowcaseView(
        visible = true,
        targetCoordinates = targetCoordinates,
        position = ShowcasePosition.Default,
        alignment = ShowcaseAlignment.CenterHorizontal,
        highlight = ShowcaseHighlight.Rectangular(cornerRadius = 16.dp),
    ) {
        Spacer(modifier = Modifier.size(1.dp))
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        contentAlignment = config.dialogAlignment,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = config.topPadding,
                        bottom = config.bottomPadding,
                    ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(id = config.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(id = config.bodyRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onSkip) {
                        Text(text = stringResource(id = R.string.home_showcase_skip))
                    }
                    Button(
                        onClick = onNext,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = nextButtonColor,
                                contentColor = nextButtonContentColor,
                            ),
                    ) { Text(text = stringResource(id = config.actionRes)) }
                }
            }
        }
    }
}

// Para el Preview, podemos aislar el card si quisiéramos, pero por ahora mostramos la estructura base
@Preview(showBackground = true)
@Composable
fun AppShowcaseOverlayPreview() {
    UnitWiseTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Ejemplo de título",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Este es un ejemplo de cómo se ve el diálogo de ayuda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = {}) {
                            Text(text = "Omitir")
                        }
                        Button(
                            onClick = {},
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = BrandPrimary,
                                    contentColor = DarkBackgroundMain,
                                ),
                        ) { Text(text = "Siguiente") }
                    }
                }
            }
        }
    }
}
