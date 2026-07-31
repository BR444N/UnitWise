package com.br444n.unitwise.app.core.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class AppBadgeCardColors(
    val containerColor: Color,
    val borderColor: Color,
    val iconTint: Color,
)

object AppBadgeCardDefaults {
    @Composable
    fun colors(
        containerColor: Color,
        borderColor: Color = MaterialTheme.colorScheme.primary,
        iconTint: Color = MaterialTheme.colorScheme.primary,
    ): AppBadgeCardColors =
        AppBadgeCardColors(
            containerColor = containerColor,
            borderColor = borderColor,
            iconTint = iconTint,
        )
}

@Composable
fun AppBadgeCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: AppBadgeCardColors,
    trailingContent: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.containerColor)
                .border(
                    BorderStroke(2.dp, colors.borderColor),
                    RoundedCornerShape(16.dp),
                ).padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.iconTint,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            trailingContent()
        }

        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AppBadgeCardPreview() {
    UnitWiseTheme {
        AppBadgeCard(
            title = "Preview Title",
            subtitle = "This is a preview subtitle to show how the badge looks.",
            icon = Icons.Default.Info,
            colors = AppBadgeCardDefaults.colors(containerColor = Badge),
            modifier = Modifier.padding(16.dp),
        )
    }
}
