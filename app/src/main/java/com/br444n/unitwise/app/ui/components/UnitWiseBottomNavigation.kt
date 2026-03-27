package com.br444n.unitwise.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.runningReduce

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun UnitWiseBottomNavigation(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    selectedIndex: Int = 0,
    onNavigate: (Int) -> Unit = {}
) {
    val items = listOf(
        NavigationItem(
            title = stringResource(id = R.string.home_tab),
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = stringResource(id = R.string.history_tab),
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        )
    )

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        NavigationBar(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .bottomNavBarBorder(
                    color = MaterialTheme.colorScheme.outline,
                    width = 1.dp,
                    cornerRadius = 24.dp
                )
            ,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = {
                        onNavigate(index)
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedIndex) {
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
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun rememberBottomNavVisibility(positionProvider: () -> Int): Boolean {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(positionProvider) {
        snapshotFlow { positionProvider() }
            .distinctUntilChanged()
            .runningReduce { previousPosition, currentPosition ->
                when {
                    currentPosition < previousPosition -> isVisible = true
                    currentPosition > previousPosition -> isVisible = false
                }
                currentPosition
            }
            .collect { }
    }

    return isVisible
}

@Preview(showBackground = true)
@Composable
fun UnitWiseBottomNavigationPreview() {
    UnitWiseTheme {
        UnitWiseBottomNavigation()
    }
}
