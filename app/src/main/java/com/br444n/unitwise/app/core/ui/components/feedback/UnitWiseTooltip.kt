package com.br444n.unitwise.app.core.ui.components.feedback

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitWiseTooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    content: @Composable () -> Unit,
) {
    val containerColor =
        if (isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        }
    val contentColor =
        if (isError) {
            MaterialTheme.colorScheme.onError
        } else {
            MaterialTheme.colorScheme.onPrimary
        }
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(
                positioning = TooltipAnchorPosition.Below,
            ),
        tooltip = {
            PlainTooltip(
                containerColor = containerColor,
                contentColor = contentColor,
            ) {
                Text(
                    text = tooltipText,
                    color = contentColor,
                )
            }
        },
        state = rememberTooltipState(),
        modifier = modifier,
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
fun UnitWiseTooltipPreview() {
    UnitWiseTheme {
        UnitWiseTooltip(
            tooltipText = "This is a tooltip",
        ) {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
            }
        }
    }
}
