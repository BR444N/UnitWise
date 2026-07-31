package com.br444n.unitwise.app.core.ui.components.wrappers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun AppHighlightedWrapper(
    badgeText: String,
    badgeIcon: ImageVector,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    badgeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier =
                Modifier
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp),
                    ).clip(RoundedCornerShape(16.dp)),
        ) {
            content()
        }

        Row(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = badgeColor,
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp),
                    ).padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = badgeIcon,
                contentDescription = null,
                tint = badgeContentColor,
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = badgeContentColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppHighlightedWrapperPreview() {
    UnitWiseTheme {
        AppHighlightedWrapper(
            badgeText = "BEST VALUE",
            badgeIcon = Icons.Default.Stars,
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .width(300.dp)
                        .padding(32.dp),
            ) {
                Text("Wrapped Product Card Content Placeholder")
            }
        }
    }
}
