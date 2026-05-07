package com.br444n.unitwise.app.feature.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun CalculateButton(
    modifier: Modifier = Modifier,
    text: String = "Calculate",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier,
        expanded = enabled,
        icon = { 
            Icon(
                imageVector = Icons.Default.Calculate, 
                contentDescription = null 
            ) 
        },
        text = { 
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            ) 
        },
        containerColor = if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        contentColor = if (enabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
        },
    )
}

@Preview(showBackground = true)
@Composable
fun CalculateButtonPreview() {
    UnitWiseTheme {
        CalculateButton(
            modifier = Modifier.padding(16.dp),
            onClick = {},
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculateButtonOffPreview() {
    UnitWiseTheme {
        CalculateButton(
            modifier = Modifier.padding(16.dp),
            onClick = {},
            enabled = false
        )
    }
}
