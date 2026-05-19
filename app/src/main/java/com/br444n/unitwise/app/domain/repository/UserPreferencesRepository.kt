package com.br444n.unitwise.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val isDarkTheme: Flow<Boolean>
    val selectedLanguage: Flow<String>
    val isHomeShowcaseCompleted: Flow<Boolean>
    suspend fun saveThemePreference(isDarkTheme: Boolean)
    suspend fun saveLanguagePreference(language: String)
    suspend fun saveHomeShowcaseCompleted(completed: Boolean)
}
