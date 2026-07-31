package com.br444n.unitwise.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.br444n.unitwise.app.UnitWiseApplication
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val isDarkTheme: StateFlow<Boolean?> =
        userPreferencesRepository.isDarkTheme
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val selectedLanguage: StateFlow<String?> =
        userPreferencesRepository.selectedLanguage
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null,
            )

    val seenFeatures: StateFlow<Set<String>> =
        userPreferencesRepository.seenFeatures
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptySet(),
            )

    fun markFeatureAsSeen(featureKey: String) {
        viewModelScope.launch {
            userPreferencesRepository.markFeatureAsSeen(featureKey)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as UnitWiseApplication)
                    val repository = application.container.userPreferencesRepository
                    MainViewModel(repository)
                }
            }
    }
}
