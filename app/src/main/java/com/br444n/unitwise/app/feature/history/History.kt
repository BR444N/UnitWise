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
import com.br444n.unitwise.app.feature.history.dialog.ClearHistoryDialog

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory),
    onNavigate: (Int) -> Unit = {},
    onViewDetails: (Int) -> Unit = {},
    onShareClick: (ComparisonEntity) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    HistoryContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onViewDetails = onViewDetails,
        onShareClick = onShareClick,
        onClearAllClick = { viewModel.clearAll() },
        modifier = modifier
    )
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    onNavigate: (Int) -> Unit,
    onViewDetails: (Int) -> Unit,
    onShareClick: (ComparisonEntity) -> Unit,
    onClearAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val showClearDialog = remember { mutableStateOf(false) }

    if (showClearDialog.value) {
        ClearHistoryDialog(
            onDismissRequest = { showClearDialog.value = false },
            onConfirmClick = {
                showClearDialog.value = false
                onClearAllClick()
            }
        )
    }

    val filteredComparisons = remember(uiState.comparisons, searchQuery) {
        if (searchQuery.isBlank()) uiState.comparisons
        else uiState.comparisons.filter { item ->
            item.entity.productAName.contains(searchQuery, ignoreCase = true) ||
            item.entity.productBName.contains(searchQuery, ignoreCase = true)
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
                    onClearAllClick = { showClearDialog.value = true }
                )
            }

            // Comparison cards
            items(
                items = filteredComparisons,
                key = { it.entity.id }
            ) { item ->
                HistoryComparisonCard(
                    productAName = item.entity.productAName,
                    productBName = item.entity.productBName,
                    winnerName = item.winnerName,
                    timestamp = item.entity.timestamp,
                    onViewDetailsClick = { onViewDetails(item.entity.id) },
                    onShareClick = { onShareClick(item.entity) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    UnitWiseTheme {
        HistoryContent(
            uiState = HistoryUiState(),
            onNavigate = {},
            onViewDetails = {},
            onShareClick = {},
            onClearAllClick = {}
        )
    }
}
