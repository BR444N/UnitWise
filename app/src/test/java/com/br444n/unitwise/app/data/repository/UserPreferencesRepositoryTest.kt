package com.br444n.unitwise.app.data.repository

import android.content.SharedPreferences
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [UserPreferencesRepositoryImpl].
 */
class UserPreferencesRepositoryTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setup() {
        every { sharedPreferences.edit() } returns editor
        repository = UserPreferencesRepositoryImpl(sharedPreferences)
    }

    @Test
    fun `isDarkTheme should emit initial value`() = runTest {
        // Given
        val key = "is_dark_theme"
        every { sharedPreferences.getBoolean(key, false) } returns true

        // Then
        repository.isDarkTheme.test {
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectedLanguage should emit initial value`() = runTest {
        // Given
        val key = "selected_language"
        every { sharedPreferences.getString(key, "en") } returns "es"

        // Then
        repository.selectedLanguage.test {
            assertThat(awaitItem()).isEqualTo("es")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveThemePreference should update sharedPreferences`() = runTest {
        // When
        repository.saveThemePreference(true)

        // Then
        verify {
            editor.putBoolean("is_dark_theme", true)
            editor.apply()
        }
    }

    @Test
    fun `saveLanguagePreference should update sharedPreferences`() = runTest {
        // When
        repository.saveLanguagePreference("fr")

        // Then
        verify {
            editor.putString("selected_language", "fr")
            editor.apply()
        }
    }
}
