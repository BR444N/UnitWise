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
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.buttons.AppSecondaryButton
import com.br444n.unitwise.app.feature.share.components.ComparisonShareBottomSheet
import com.br444n.unitwise.app.navigation.components.rememberBottomNavVisibility
import com.br444n.unitwise.app.navigation.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.core.ui.components.dialogs.AppDialog
import com.br444n.unitwise.app.core.ui.components.dialogs.AppDialogConfig
import com.br444n.unitwise.app.core.ui.components.states.AppEmptyState
import com.br444n.unitwise.app.core.ui.components.inputs.AppSearchBar
import com.br444n.unitwise.app.core.ui.components.cards.AppComparisonCard
import com.br444n.unitwise.app.core.ui.components.cards.AppComparisonCardConfig
import com.br444n.unitwise.app.core.ui.components.cards.AppComparisonCardActions

private const val HISTORY_TITLE_WRAP_THRESHOLD = 20

private fun formatComparisonTitle(
    productAName: String,
    productBName: String,
    defaultProductA: String,
    defaultProductB: String
): String {
    val firstName = productAName.ifBlank { defaultProductA }
    val secondName = productBName.ifBlank { defaultProductB }

    return if (firstName.length > HISTORY_TITLE_WRAP_THRESHOLD || secondName.length > HISTORY_TITLE_WRAP_THRESHOLD) {
        "$firstName vs\n$secondName"
    } else {
        "$firstName vs $secondName"
    }
}

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
    
    val defaultProductA = stringResource(R.string.comparison_default_product_a)
    val defaultProductB = stringResource(R.string.comparison_default_product_b)

    if (showClearDialog.value) {
        AppDialog(
            config = AppDialogConfig(
                title = stringResource(id = R.string.clear_history_dialog_title),
                confirmText = stringResource(id = R.string.delete),
                isErrorAction = true
            ),
            onDismissRequest = { showClearDialog.value = false },
            onConfirmClick = {
                showClearDialog.value = false
                onClearAllClick()
            }
        ) {
            Text(
                text = stringResource(id = R.string.clear_history_dialog_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
                AppTopBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Badge)
                                    .border(
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                        RoundedCornerShape(50.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null, // decorative
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.history_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> {
                    // Keep background clean while loading to avoid flickers
                }
                uiState.comparisons.isEmpty() -> {
                    AppEmptyState(
                        title = stringResource(id = R.string.history_empty_title),
                        subtitle = stringResource(id = R.string.history_empty_subtitle),
                        iconResId = R.drawable.no_comparisons,
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
                            AppSearchBar(
                                query = searchQueryState.value,
                                onQueryChange = { searchQueryState.value = it },
                                hint = stringResource(R.string.search_comparison_hint),
                                modifier = Modifier.padding(horizontal = 0.dp)
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.recent_comparisons),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                AppSecondaryButton(
                                    text = stringResource(id = R.string.clear_all),
                                    onClick = { showClearDialog.value = true }
                                )
                            }
                        }

                        items(
                            items = filteredComparisons,
                            key = { it.entity.id }
                        ) { item ->
                            
                            val title = formatComparisonTitle(
                                item.entity.productAName,
                                item.entity.productBName,
                                defaultProductA,
                                defaultProductB
                            )
                            
                            val microBadgeText = if (item.winnerName == null) {
                                stringResource(id = R.string.tie_title) 
                            } else {
                                stringResource(id = R.string.best_value_micro_badge, item.winnerName)
                            }
                            
                            AppComparisonCard(
                                config = AppComparisonCardConfig(
                                    title = title,
                                    timestamp = item.entity.timestamp,
                                    badgeText = microBadgeText,
                                    primaryActionText = stringResource(id = R.string.view_details),
                                    secondaryActionText = stringResource(id = R.string.share),
                                    primaryActionIcon = Icons.AutoMirrored.Filled.ArrowForward,
                                    secondaryActionIcon = Icons.Default.Share
                                ),
                                actions = AppComparisonCardActions(
                                    onEditClick = { onEditComparison(item.entity.id) },
                                    onPrimaryActionClick = { onViewDetails(item.entity.id) },
                                    onSecondaryActionClick = { selectedComparisonToShareState.value = item.entity }
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
            selectedIndex = 2,
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
