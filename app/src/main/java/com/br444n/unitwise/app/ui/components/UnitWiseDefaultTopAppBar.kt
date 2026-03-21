package com.br444n.unitwise.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitWiseDefaultTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationContentDescription: String? = null,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        navigationIcon = {
            if (navigationContentDescription != null) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        positioning = TooltipAnchorPosition.Below
                    ),
                    tooltip = {
                        PlainTooltip(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = navigationContentDescription,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = navigationContentDescription,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .topAppBarBorder(
                color = MaterialTheme.colorScheme.outline,
                width = 1.dp,
                cornerRadius = 24.dp
            )
    )
}

@Preview(showBackground = true)
@Composable
fun UnitWiseDefaultTopAppBarPreview() {
    UnitWiseTheme {
        UnitWiseDefaultTopAppBar(
            title = "Configuración",
            onBackClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun UnitWiseDefaultTopAppBarEmptyPreview() {
    UnitWiseTheme {
        UnitWiseDefaultTopAppBar(
            onBackClick = {}
        )
    }
}
