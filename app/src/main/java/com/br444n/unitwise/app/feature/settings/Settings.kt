package com.br444n.unitwise.app.feature.settings

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.BuildConfig
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.Constants
import com.br444n.unitwise.app.core.ui.components.lists.AppListDivider
import com.br444n.unitwise.app.core.ui.components.lists.AppListItem
import com.br444n.unitwise.app.core.ui.components.lists.AppListSectionTitle
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import com.br444n.unitwise.app.core.ui.components.cards.AppHeaderCard
import androidx.compose.ui.res.painterResource
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

private data class LanguageItem(
    val code: String,
    val nameResId: Int
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    onBackClick: () -> Unit,
    onNavigate: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        modifier = modifier,
        uiState = uiState,
        onToggleTheme = viewModel::toggleTheme,
        onLanguageSelected = viewModel::updateLanguage,
        onBackClick = onBackClick,
        onNavigate = onNavigate
    )
}

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onToggleTheme: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_desc),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    SettingsBackButton(
                        contentDesc = stringResource(id = R.string.navigate_up),
                        onClick = onBackClick
                    )
                }
            )
        },

        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            item { SettingsAppearanceSection(uiState.isDarkTheme, onToggleTheme) }
            item { AppListDivider() }
            
            item { SettingsPreferencesSection(uiState.selectedLanguage, onLanguageSelected) }
            item { AppListDivider() }
            
            item { SettingsPrivacySection() }
            item { AppListDivider() }
            
            item { SettingsAboutSection() }
            
            // Branding Footer
            item { 
                AppHeaderCard(
                    imagePainter = painterResource(id = R.drawable.splash_icon),
                    tagline = stringResource(id = R.string.settings_header_tagline),
                    imageBackgroundColor = Badge
                )
            }
        }
    }
}

@Composable
private fun SettingsAppearanceSection(isDarkTheme: Boolean, onToggleTheme: (Boolean) -> Unit) {
    AppListSectionTitle(text = stringResource(id = R.string.settings_appearance))
    AppListItem(
        title = stringResource(id = R.string.settings_dark_theme_title),
        subtitle = stringResource(id = R.string.settings_dark_theme_subtitle),
        icon = Icons.Default.DarkMode,
        onClick = { onToggleTheme(!isDarkTheme) },
        trailingContent = {
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onToggleTheme,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = Badge,
                    uncheckedBorderColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    )
}

@Composable
private fun SettingsPreferencesSection(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    var expandedLanguageDropdown by remember { mutableStateOf(false) }
    
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

    val currentLanguageName = languages.find { it.code == selectedLanguage }?.nameResId?.let { 
        stringResource(it) 
    } ?: stringResource(R.string.lang_en)

    AppListSectionTitle(text = stringResource(id = R.string.settings_preferences))
    AppListItem(
        title = stringResource(id = R.string.settings_language_title),
        subtitle = stringResource(id = R.string.settings_language_subtitle),
        icon = Icons.Default.Language,
        onClick = { expandedLanguageDropdown = true },
        trailingContent = {
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
                
                LanguageDropdownMenu(
                    expanded = expandedLanguageDropdown,
                    onDismissRequest = { expandedLanguageDropdown = false },
                    languages = languages,
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { 
                        onLanguageSelected(it)
                        expandedLanguageDropdown = false
                    }
                )
            }
        }
    )
}

@Composable
private fun LanguageDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    languages: List<LanguageItem>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        languages.forEachIndexed { index, languageItem ->
            val isSelected = selectedLanguage == languageItem.code
            
            DropdownMenuItem(
                text = { 
                    Text(
                        text = stringResource(languageItem.nameResId),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                onClick = { onLanguageSelected(languageItem.code) }
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

@Composable
private fun SettingsPrivacySection() {
    val context = LocalContext.current
    val chooserTitle = stringResource(id = R.string.settings_privacy_policy)
    
    AppListSectionTitle(text = stringResource(id = R.string.settings_privacy_policy))
    AppListItem(
        title = stringResource(id = R.string.settings_privacy_policy_2),
        subtitle = stringResource(id = R.string.settings_privacy_policy_description),
        icon = Icons.Default.PrivacyTip,
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Constants.PRIVACY_POLICY_URL.toUri())
            val chooser = Intent.createChooser(intent, chooserTitle)
            context.startActivity(chooser)
        }
    )
}

@Composable
private fun SettingsAboutSection() {
    val context = LocalContext.current
    val chooserTitle = stringResource(id = R.string.settings_developer_title)
    
    AppListSectionTitle(text = stringResource(id = R.string.settings_about))
    AppListItem(
        title = stringResource(id = R.string.settings_version_title),
        subtitle = stringResource(id = R.string.settings_version_subtitle, BuildConfig.VERSION_NAME),
        icon = Icons.Default.Info
    )
    AppListItem(
        title = stringResource(id = R.string.settings_developer_title),
        subtitle = stringResource(id = R.string.settings_developer_subtitle),
        icon = Icons.Default.Terminal,
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Constants.DEVELOPER_URL.toUri())
            val chooser = Intent.createChooser(intent, chooserTitle)
            context.startActivity(chooser)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    UnitWiseTheme {
        SettingsContent(
            uiState = SettingsUiState(
                isDarkTheme = false,
                selectedLanguage = "en"
            ),
            onToggleTheme = {},
            onLanguageSelected = {},
            onBackClick = {},
            onNavigate = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsBackButton(contentDesc: String, onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = contentDesc,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
