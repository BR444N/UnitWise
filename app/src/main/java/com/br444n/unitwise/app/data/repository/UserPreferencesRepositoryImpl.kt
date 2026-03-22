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
                trySend(prefs.getString(SELECTED_LANGUAGE, "English") ?: "English")
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getString(SELECTED_LANGUAGE, "English") ?: "English")
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

    companion object {
        private const val IS_DARK_THEME = "is_dark_theme"
        private const val SELECTED_LANGUAGE = "selected_language"
    }
}
