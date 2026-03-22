package com.br444n.unitwise.app.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

private data class LanguageItem(
    val code: String,
    val nameResId: Int
)

@Composable
fun LanguageSelectorCard(
    modifier: Modifier = Modifier,
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val languages = remember {
        listOf(
            LanguageItem("en", R.string.lang_en),
            LanguageItem("es", R.string.lang_es),
            LanguageItem("fr", R.string.lang_fr),
            LanguageItem("de", R.string.lang_de),
            LanguageItem("it", R.string.lang_it),
            LanguageItem("pt", R.string.lang_pt)
        )
    }

    val currentLanguageName = languages.find { it.code == selectedLanguageCode }?.nameResId?.let { 
        stringResource(it) 
    } ?: stringResource(R.string.lang_en)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Badge),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.settings_language_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.settings_language_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LanguageDropdownSection(
            currentLanguageName = currentLanguageName,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            languages = languages,
            selectedLanguageCode = selectedLanguageCode,
            onLanguageSelected = onLanguageSelected
        )
    }
}

@Composable
private fun LanguageDropdownSection(
    currentLanguageName: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    languages: List<LanguageItem>,
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    // Dropdown Trigger (Text + Arrow)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = currentLanguageName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            languages.forEachIndexed { index, languageItem ->
                val isSelected = selectedLanguageCode == languageItem.code
                
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = stringResource(languageItem.nameResId),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    onClick = {
                        onLanguageSelected(languageItem.code)
                        onExpandedChange(false)
                    }
                )
                
                if (index < languages.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = Badge.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectorCardPreview() {
    UnitWiseTheme {
        LanguageSelectorCard(
            selectedLanguageCode = "en",
            onLanguageSelected = {}
        )
    }
}
