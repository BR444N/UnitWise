package com.br444n.unitwise.app.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun HomeHeaderText(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section Title: Medium (500) based on Type.md
        // We use titleMedium which is mapped to SemiBold (600) for screen titles / App Identity
        // Wait, Type.md says: 
        // 2. Section Titles -> Weight: Medium (500)
        // 1. App Identity / Screen titles -> SemiBold (600)
        // Let's use titleLarge for the main header (SemiBold) and bodyLarge/titleMedium for subtitle
        Text(
            text = stringResource(id = R.string.home_header_title),
            style = MaterialTheme.typography.titleLarge, // SemiBold 600
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Subtitle Text: Regular (400) or Medium (500) based on Subtitles vs BodyText
        // Let's use bodyLarge (Regular 400) or labelLarge based on Type.kt mappings
        // Type.md -> Labels & Subtitles = Medium (500) mappings: labelMedium or titleMedium
        // Since it's a page subtitle, bodyLarge with a specific color is often used, 
        // but we'll stick to bodyLarge for normal readability or labelMedium if we want it strictly medium.
        // Let's use bodyLarge to match the 16sp size but apply the Subtitle Text color
        Text(
            text = stringResource(id = R.string.home_header_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant // Subtitle Text #64748B
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F8F6)
@Composable
fun HomeHeaderTextPreview() {
    UnitWiseTheme {
        HomeHeaderText(
            modifier = Modifier.padding(16.dp)
        )
    }
}
