package com.br444n.unitwise.app.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.feature.history.components.HistoryComparisonCardActions
import com.br444n.unitwise.app.feature.history.components.HistoryComparisonCard
import com.br444n.unitwise.app.feature.history.components.HistoryEmptyState
import com.br444n.unitwise.app.feature.history.components.HistorySearchBar
import com.br444n.unitwise.app.feature.history.components.HistorySectionHeader
import com.br444n.unitwise.app.feature.history.components.HistoryTopAppBar
import com.br444n.unitwise.app.feature.share.components.ComparisonShareBottomSheet
import com.br444n.unitwise.app.ui.components.rememberBottomNavVisibility
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.feature.history.dialog.ClearHistoryDialog

private val HistoryBottomNavOverlayPadding = 96.dp

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory),
    onNavigate: (Int) -> Unit = {},
    onViewDetails: (Int) -> Unit = {},
    onEditComparison: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    HistoryContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onViewDetails = onViewDetails,
        onEditComparison = onEditComparison,
        onClearAllClick = { viewModel.clearAll() },
        modifier = modifier
    )
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    onNavigate: (Int) -> Unit,
    onViewDetails: (Int) -> Unit,
    onEditComparison: (Int) -> Unit,
    onClearAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQueryState = remember { mutableStateOf("") }
    val selectedComparisonToShareState = remember { mutableStateOf<ComparisonEntity?>(null) }
    val showClearDialog = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val isBottomNavVisible = rememberBottomNavVisibility {
        (listState.firstVisibleItemIndex * 100_000) + listState.firstVisibleItemScrollOffset
    }

    if (showClearDialog.value) {
        ClearHistoryDialog(
            onDismissRequest = { showClearDialog.value = false },
            onConfirmClick = {
                showClearDialog.value = false
                onClearAllClick()
            }
        )
    }

    val filteredComparisons = remember(uiState.comparisons, searchQueryState.value) {
        if (searchQueryState.value.isBlank()) uiState.comparisons
        else uiState.comparisons.filter { item ->
            item.entity.productAName.contains(searchQueryState.value, ignoreCase = true) ||
            item.entity.productBName.contains(searchQueryState.value, ignoreCase = true)
        }
    }

    selectedComparisonToShareState.value?.let { comparison ->
        ComparisonShareBottomSheet(
            comparison = comparison,
            onDismissRequest = { selectedComparisonToShareState.value = null }
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                HistoryTopAppBar()
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> {
                    // Keep background clean while loading to avoid flickers
                }
                uiState.comparisons.isEmpty() -> {
                    HistoryEmptyState(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = HistoryBottomNavOverlayPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            HistorySearchBar(
                                query = searchQueryState.value,
                                onQueryChange = { searchQueryState.value = it },
                                modifier = Modifier.padding(horizontal = 0.dp)
                            )
                        }

                        item {
                            HistorySectionHeader(
                                onClearAllClick = { showClearDialog.value = true }
                            )
                        }

                        items(
                            items = filteredComparisons,
                            key = { it.entity.id }
                        ) { item ->
                        HistoryComparisonCard(
                            productAName = item.entity.productAName,
                            productBName = item.entity.productBName,
                            winnerName = item.winnerName,
                            timestamp = item.entity.timestamp,
                            actions = HistoryComparisonCardActions(
                                onEditClick = { onEditComparison(item.entity.id) },
                                onViewDetailsClick = { onViewDetails(item.entity.id) },
                                onShareClick = { selectedComparisonToShareState.value = item.entity }
                            )
                        )
                        }
                    }
                }
            }
        }

        UnitWiseBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            visible = isBottomNavVisible,
            selectedIndex = 1,
            onNavigate = onNavigate
        )
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
            onEditComparison = {},
            onClearAllClick = {}
        )
    }
}
