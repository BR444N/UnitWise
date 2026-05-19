package com.br444n.unitwise.app.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserPreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : UserPreferencesRepository {

    override val isDarkTheme: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == IS_DARK_THEME) {
                trySend(prefs.getBoolean(IS_DARK_THEME, false))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getBoolean(IS_DARK_THEME, false))
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override val selectedLanguage: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == SELECTED_LANGUAGE) {
                trySend(prefs.getString(SELECTED_LANGUAGE, "en") ?: "en")
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getString(SELECTED_LANGUAGE, "en") ?: "en")
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override val isHomeShowcaseCompleted: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == HOME_SHOWCASE_COMPLETED) {
                trySend(prefs.getBoolean(HOME_SHOWCASE_COMPLETED, false))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getBoolean(HOME_SHOWCASE_COMPLETED, false))
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override suspend fun saveThemePreference(isDarkTheme: Boolean) {
        sharedPreferences.edit { putBoolean(IS_DARK_THEME, isDarkTheme) }
    }

    override suspend fun saveLanguagePreference(language: String) {
        sharedPreferences.edit { putString(SELECTED_LANGUAGE, language) }
    }

    override suspend fun saveHomeShowcaseCompleted(completed: Boolean) {
        sharedPreferences.edit { putBoolean(HOME_SHOWCASE_COMPLETED, completed) }
    }

    companion object {
        private const val IS_DARK_THEME = "is_dark_theme"
        private const val SELECTED_LANGUAGE = "selected_language"
        private const val HOME_SHOWCASE_COMPLETED = "home_showcase_completed"
    }
}
