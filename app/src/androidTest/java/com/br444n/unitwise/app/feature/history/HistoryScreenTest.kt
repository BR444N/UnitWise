package com.br444n.unitwise.app.feature.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.br444n.unitwise.R
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import org.junit.Rule
import org.junit.Test

/**
 * Instrumentation tests for [HistoryScreen].
 */
class HistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun historyEmptyState_isDisplayed_whenComparisonsAreEmpty() {
        // Given
        val uiState = HistoryUiState(
            comparisons = emptyList(),
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            UnitWiseTheme {
                HistoryContent(
                    uiState = uiState,
                    onNavigate = {},
                    onViewDetails = {},
                    onShareClick = {},
                    onClearAllClick = {}
                )
            }
        }

        // Then
        val emptyTitle = context.getString(R.string.history_empty_title)
        composeTestRule.onNodeWithText(emptyTitle).assertIsDisplayed()
    }

    @Test
    fun historyList_isDisplayed_whenComparisonsArePresent() {
        // Given
        val comparisons = listOf(
            HistoryItemUiModel(
                entity = ComparisonEntity(
                    id = 1,
                    productAName = "Product A",
                    productBName = "Product B",
                    productAContent = "100",
                    productBContent = "200",
                    productAUnit = "g",
                    productBUnit = "g",
                    productAPrice = "10.0",
                    productBPrice = "15.0",
                    productAQuantity = "1",
                    productBQuantity = "1",
                    timestamp = System.currentTimeMillis()
                ),
                winnerName = "Product B"
            )
        )
        val uiState = HistoryUiState(
            comparisons = comparisons,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            UnitWiseTheme {
                HistoryContent(
                    uiState = uiState,
                    onNavigate = {},
                    onViewDetails = {},
                    onShareClick = {},
                    onClearAllClick = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Product A vs Product B").assertIsDisplayed()
        val bestValueText = context.getString(R.string.best_value_micro_badge, "Product B")
        composeTestRule.onNodeWithText(bestValueText).assertIsDisplayed()
    }
}
