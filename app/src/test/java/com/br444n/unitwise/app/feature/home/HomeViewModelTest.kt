package com.br444n.unitwise.app.feature.home

import com.br444n.unitwise.app.domain.usecase.GetComparisonUseCase
import com.br444n.unitwise.app.domain.usecase.SaveComparisonUseCase
import com.br444n.unitwise.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [HomeViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saveComparisonUseCase = mockk<SaveComparisonUseCase>()
    private val getComparisonUseCase = mockk<GetComparisonUseCase>()

    @Test
    fun `calculate should save and navigate after delay`() = runTest {
        // Given
        val expectedId = 42L
        coEvery { saveComparisonUseCase(any(), any(), any()) } returns expectedId
        val viewModel = HomeViewModel(saveComparisonUseCase, getComparisonUseCase)
        var navigatedId: Int? = null

        // When
        viewModel.calculate { id -> navigatedId = id }

        // Then - Initial loading state
        assertThat(viewModel.uiState.value.isLoading).isTrue()

        // Advance time by the calculation delay (1500ms)
        testScheduler.advanceTimeBy(1600)

        // Then - Final state
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(navigatedId).isEqualTo(expectedId.toInt())
        
        // Verify products are reset to default state
        assertThat(viewModel.uiState.value.productA.productName).isEmpty()
        assertThat(viewModel.uiState.value.productB.productName).isEmpty()
        assertThat(viewModel.uiState.value.productA.selectedUnit).isEqualTo("g")
        assertThat(viewModel.uiState.value.productA.quantity).isEqualTo("1")
    }
}
