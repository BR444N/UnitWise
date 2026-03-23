package com.br444n.unitwise.app.domain.usecase

import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for [CompareProductsUseCase].
 */
class CompareProductsUseCaseTest {

    private val useCase = CompareProductsUseCase()

    @Test
    fun `product A is cheaper than product B`() {
        // Given
        val productA = ProductInputState(
            productName = "Product A",
            contentAmount = "1000",
            selectedUnit = "g",
            price = "10.0",
            quantity = "1"
        ) // PPU = 0.01 per g
        val productB = ProductInputState(
            productName = "Product B",
            contentAmount = "500",
            selectedUnit = "g",
            price = "6.0",
            quantity = "1"
        ) // PPU = 0.012 per g

        // When
        val result = useCase(productA, productB)

        // Then
        assertThat(result.isProductAWinner).isTrue()
        assertThat(result.isTie).isFalse()
        // Savings = (1000g * 0.012 [B_PPU]) - 10.0 [A_ActualCost] = 12.0 - 10.0 = 2.00
        assertThat(result.savingsTotal).isEqualTo("2.00")
    }

    @Test
    fun `product B is cheaper than product A when weights are different types`() {
        // Given
        val productA = ProductInputState(
            productName = "Product A",
            contentAmount = "1",
            selectedUnit = "kg",
            price = "20.0",
            quantity = "1"
        ) // 1000g, PPU = 0.02 per g
        val productB = ProductInputState(
            productName = "Product B",
            contentAmount = "2000",
            selectedUnit = "g",
            price = "30.0",
            quantity = "1"
        ) // 2000g, PPU = 0.015 per g

        // When
        val result = useCase(productA, productB)

        // Then
        assertThat(result.isProductAWinner).isFalse()
        assertThat(result.isTie).isFalse()
        // Winner PPU: 0.015, Loser PPU: 0.02
        // Volume: 2000g. Cost if bought loser: 2000 * 0.02 = 40.0
        // Actual cost of winner: 30.0. Savings: 10.00
        assertThat(result.savingsTotal).isEqualTo("10.00")
    }

    @Test
    fun `it is a tie when prices per unit are equal`() {
        // Given
        val productA = ProductInputState(
            productName = "Product A",
            contentAmount = "100",
            selectedUnit = "g",
            price = "1.0",
            quantity = "1"
        ) // 0.01 per g
        val productB = ProductInputState(
            productName = "Product B",
            contentAmount = "500",
            selectedUnit = "g",
            price = "5.0",
            quantity = "1"
        ) // 0.01 per g

        // When
        val result = useCase(productA, productB)

        // Then
        assertThat(result.isTie).isTrue()
        assertThat(result.savingsTotal).isEqualTo("0.00")
    }
}
