package com.br444n.unitwise.app.feature.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.BuildConfig
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.settings.components.AppVersionCard
import com.br444n.unitwise.app.feature.settings.components.DeveloperInfoCard
import com.br444n.unitwise.app.feature.settings.components.PrivacyPolicyCard
import com.br444n.unitwise.app.feature.settings.components.LanguageSelectorCard
import com.br444n.unitwise.app.feature.settings.components.SettingsHeaderCard
import com.br444n.unitwise.app.feature.settings.components.ToggleThemeCard
import com.br444n.unitwise.app.feature.settings.components.titles.SettingsDivider
import com.br444n.unitwise.app.feature.settings.components.titles.SettingsSectionTitle
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import com.br444n.unitwise.app.navigation.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

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
        bottomBar = {
            UnitWiseBottomNavigation(
                selectedIndex = 2, // No tab selected as it's not in the main 2 tabs
                onNavigate = onNavigate
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            // APPEARANCE SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_appearance))
            }
            item {
                ToggleThemeCard(
                    isDarkTheme = uiState.isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }

            item { SettingsDivider() }

            // PREFERENCES SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_preferences))
            }
            item {
                LanguageSelectorCard(
                    selectedLanguageCode = uiState.selectedLanguage,
                    onLanguageSelected = onLanguageSelected
                )
            }

            item { SettingsDivider() }

            // PRIVACY SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_privacy_policy))
            }
            item {
                PrivacyPolicyCard()
            }

            item { SettingsDivider() }

            // ABOUT SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_about))
            }
            item {
                AppVersionCard(versionName = BuildConfig.VERSION_NAME)
            }
            item {
                DeveloperInfoCard()
            }
            
            // Branding Footer
            item {
                SettingsHeaderCard()
            }
        }
    }
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
