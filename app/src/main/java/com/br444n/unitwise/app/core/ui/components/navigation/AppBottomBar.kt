package com.br444n.unitwise.app.core.ui.components.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.components.bottomNavBarBorder
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .bottomNavBarBorder(
                color = MaterialTheme.colorScheme.outline,
                width = 1.dp,
                cornerRadius = 24.dp
            ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    UnitWiseTheme {
        AppBottomBar{ }
    }
}
