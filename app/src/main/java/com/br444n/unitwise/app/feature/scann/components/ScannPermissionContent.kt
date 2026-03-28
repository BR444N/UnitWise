package com.br444n.unitwise.app.feature.scann.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ScannPermissionContent(
    onBackClick: () -> Unit,
    title: String,
    description: String,
    actionLabel: String?,
    onActionClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ScannTopAppBar(
            modifier = Modifier.align(Alignment.TopCenter),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onActionClick) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannPermissionContentPreview() {
    UnitWiseTheme {
        ScannPermissionContent(
            onBackClick = {},
            title = "Camera access required",
            description = "Allow camera access to scan product names.",
            actionLabel = "Allow camera",
            onActionClick = {}
        )
    }
}
