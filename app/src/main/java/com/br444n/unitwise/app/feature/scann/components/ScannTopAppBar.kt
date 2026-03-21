package com.br444n.unitwise.app.feature.scann.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ScannTopAppBar(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean = false,
    onBackClick: () -> Unit = {},
    onFlashClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button with Shadow
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.navigate_up),
                tint = Color.White
            )
        }

        // Flash Button with Shadow
        IconButton(
            onClick = onFlashClick,
            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = stringResource(id = R.string.toggle_flash),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF555555)
@Composable
fun ScanTopAppBarPreview() {
    UnitWiseTheme {
        ScannTopAppBar()
    }
}
