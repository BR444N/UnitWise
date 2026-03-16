package com.br444n.unitwise.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun UnitWiseBottomNavigation(
    modifier: Modifier = Modifier,
    onNavigate: (Int) -> Unit = {}
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) } // 0 is Home (default)

    val items = listOf(
        NavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = "History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        )
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    onNavigate(index)
                },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) {
                            item.selectedIcon
                        } else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title, style = MaterialTheme.typography.labelMedium)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary, // Green text
                    indicatorColor = MaterialTheme.colorScheme.primary, // Green pill
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Slate Blue
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant // Slate Blue
                )
            )
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