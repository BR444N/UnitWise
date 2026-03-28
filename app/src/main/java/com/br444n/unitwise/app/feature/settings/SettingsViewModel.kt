package com.br444n.unitwise.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import com.br444n.unitwise.app.ui.util.LocaleHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferencesRepository.isDarkTheme,
        userPreferencesRepository.selectedLanguage
    ) { isDark, language ->
        SettingsUiState(isDarkTheme = isDark, selectedLanguage = language)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(isDark)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveLanguagePreference(
                LocaleHelper.normalizeLanguageCode(language)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                val repository = application.container.userPreferencesRepository
                SettingsViewModel(repository)
            }
        }
    }
}
