package com.br444n.unitwise.app.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.feature.history.components.HistoryComparisonCard
import com.br444n.unitwise.app.feature.history.components.HistorySearchBar
import com.br444n.unitwise.app.feature.history.components.HistorySectionHeader
import com.br444n.unitwise.app.feature.history.components.HistoryTopAppBar
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory),
    onNavigate: (Int) -> Unit = {},
    onViewDetails: (Int) -> Unit = {},
    onShareClick: (ComparisonEntity) -> Unit = {},
    onClearAll: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredComparisons = remember(uiState.comparisons, searchQuery) {
        if (searchQuery.isBlank()) uiState.comparisons
        else uiState.comparisons.filter { entity ->
            entity.productAName.contains(searchQuery, ignoreCase = true) ||
            entity.productBName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HistoryTopAppBar()
        },
        bottomBar = {
            UnitWiseBottomNavigation(
                selectedIndex = 1,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search bar (sticky-ish, first item)
            item {
                HistorySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }

            // Section header
            item {
                HistorySectionHeader(
                    onClearAllClick = onClearAll
                )
            }

            // Comparison cards
            items(
                items = filteredComparisons,
                key = { it.id }
            ) { entity ->
                // Determine winner by comparing PPU directly
                // For display purposes, we just show productAName as winner fallback
                val winnerName = entity.productAName.ifBlank { entity.productBName }

                HistoryComparisonCard(
                    productAName = entity.productAName,
                    productBName = entity.productBName,
                    winnerName = winnerName,
                    timestamp = entity.timestamp,
                    onViewDetailsClick = { onViewDetails(entity.id) },
                    onShareClick = { onShareClick(entity) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    UnitWiseTheme {
        // In preview, no ViewModel factory is available — screen renders with empty state
        HistoryScreen()
    }
}
