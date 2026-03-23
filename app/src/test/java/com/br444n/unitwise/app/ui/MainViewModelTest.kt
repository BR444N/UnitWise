package com.br444n.unitwise.app.ui

import app.cash.turbine.test
import com.br444n.unitwise.app.domain.repository.UserPreferencesRepository
import com.br444n.unitwise.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [MainViewModel].
 */
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UserPreferencesRepository>()
    private val themeFlow = MutableStateFlow(false)
    private val langFlow = MutableStateFlow("en")

    @Before
    fun setup() {
        every { repository.isDarkTheme } returns themeFlow
        every { repository.selectedLanguage } returns langFlow
    }

    @Test
    fun `isDarkTheme should reflect repository value`() = runTest {
        // Given
        val viewModel = MainViewModel(repository)

        // Then
        viewModel.isDarkTheme.test {
            // Ignore the initial null and get the current value from repository
            assertThat(expectMostRecentItem()).isFalse()
            
            // Update value
            themeFlow.value = true
            assertThat(awaitItem()).isTrue()
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectedLanguage should reflect repository value`() = runTest {
        // Given
        val viewModel = MainViewModel(repository)

        // Then
        viewModel.selectedLanguage.test {
            // Ignore the initial null and get the current value from repository
            assertThat(expectMostRecentItem()).isEqualTo("en")
            
            // Update value
            langFlow.value = "es"
            assertThat(awaitItem()).isEqualTo("es")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
