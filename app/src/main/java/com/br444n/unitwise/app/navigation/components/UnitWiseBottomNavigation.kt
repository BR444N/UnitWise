package com.br444n.unitwise.app.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.cards.AppMicroBadge
import com.br444n.unitwise.app.core.ui.components.feedback.UnitWiseTooltip
import com.br444n.unitwise.app.core.ui.components.navigation.AppBottomBar
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val featureKey: String? = null,
)

@Composable
fun UnitWiseBottomNavigation(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    seenFeatures: Set<String> = emptySet(),
    onFeatureClick: (String) -> Unit = {},
    onNavigate: (Int) -> Unit = {},
) {
    val items =
        listOf(
            NavigationItem(
                title = stringResource(id = R.string.home_tab),
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
            ),
            NavigationItem(
                title = stringResource(id = R.string.list),
                selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                featureKey = "feature_lists_v130",
            ),
            NavigationItem(
                title = stringResource(id = R.string.history_tab),
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History,
            ),
        )

    AppBottomBar(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    item.featureKey?.let { key ->
                        if (!seenFeatures.contains(key)) onFeatureClick(key)
                    }
                    onNavigate(index)
                },
                icon = {
                    BottomNavigationIcon(
                        item = item,
                        isSelected = isSelected,
                        isFeatureSeen =
                            item.featureKey == null || seenFeatures.contains(item.featureKey),
                    )
                },
                label = {
                    Text(text = item.title, style = MaterialTheme.typography.labelMedium)
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        }
    }
}

@Composable
private fun BottomNavigationIcon(
    item: NavigationItem,
    isSelected: Boolean,
    isFeatureSeen: Boolean,
) {
    UnitWiseTooltip(
        tooltipText = item.title,
    ) {
        Box(
            modifier =
                Modifier
                    .width(64.dp)
                    .height(32.dp)
                    .background(
                        color =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            },
                        shape = RoundedCornerShape(12.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
            )

            if (!isFeatureSeen) {
                AppMicroBadge(
                    text = stringResource(id = R.string.feature_badge_new),
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 16.dp, y = (-12).dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitWiseBottomNavigationPreview() {
    UnitWiseTheme {
        UnitWiseBottomNavigation()
    }
}
