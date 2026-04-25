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
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.settings.components.AppVersionCard
import com.br444n.unitwise.app.feature.settings.components.DeveloperInfoCard
import com.br444n.unitwise.app.feature.settings.components.PrivacyPolicyCard
import com.br444n.unitwise.app.feature.settings.components.LanguageSelectorCard
import com.br444n.unitwise.app.feature.settings.components.SettingsHeaderCard
import com.br444n.unitwise.app.feature.settings.components.ToggleThemeCard
import com.br444n.unitwise.app.feature.settings.components.titles.SettingsDivider
import com.br444n.unitwise.app.feature.settings.components.titles.SettingsSectionTitle
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseDefaultTopAppBar
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

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
            UnitWiseDefaultTopAppBar(
                title = stringResource(id = R.string.settings_desc),
                navigationContentDescription = stringResource(id = R.string.navigate_up),
                onBackClick = onBackClick
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

            // ABOUT SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_about))
            }
            item {
                AppVersionCard(versionName = "1.0")
            }
            item {
                DeveloperInfoCard()
            }

            item { SettingsDivider() }

            // PRIVACY SECTION
            item {
                SettingsSectionTitle(text = stringResource(id = R.string.settings_privacy_policy))
            }
            item {
                PrivacyPolicyCard()
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
