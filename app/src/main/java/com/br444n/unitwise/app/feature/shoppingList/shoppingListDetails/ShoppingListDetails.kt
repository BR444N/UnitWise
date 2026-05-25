package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity
import com.br444n.unitwise.app.core.ui.components.buttons.AppFloatingActionButton
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.AddItemDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.DeleteItemDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListBadges
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListItemCard
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListSavingsCard
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListSearchBar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.stringResource
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListOrphanCard
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListDetailsEmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit,
    viewModel: ShoppingListDetailsViewModel = viewModel(factory = ShoppingListDetailsViewModel.Factory)
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemIds by remember { mutableStateOf(emptySet<Int>()) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ShoppingListDetailsTopBarHandler(
                selectedItemIds = selectedItemIds,
                uiState = uiState,
                onNavigateBack = onNavigateBack,
                onCancelSelection = { selectedItemIds = emptySet() },
                onSelectAll = { selectedItemIds = uiState.items.map { it.id }.toSet() },
                onDeleteSelected = { showDeleteDialog = true }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                text = stringResource(id = R.string.add_category_button),
                icon = Icons.Default.Add,
                onClick = { showAddDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ShoppingListSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoppingListDetailsHeader(uiState = uiState)

            val isSearchActive = searchQuery.isNotBlank()
            val filteredItems = if (isSearchActive) {
                uiState.items.filter { it.categoryName.contains(searchQuery, ignoreCase = true) }
            } else {
                uiState.items
            }

            ShoppingListDetailsList(
                items = filteredItems,
                isSearchActive = isSearchActive,
                selectedItemIds = selectedItemIds,
                onToggleSelection = { itemId, isSelected ->
                    selectedItemIds = if (isSelected) {
                        selectedItemIds - itemId
                    } else {
                        selectedItemIds + itemId
                    }
                },
                onNavigateToCompare = onNavigateToCompare
            )
        }

        ShoppingListDetailsDialogs(
            showAddDialog = showAddDialog,
            showDeleteDialog = showDeleteDialog,
            onDismissAddDialog = { showAddDialog = false },
            onConfirmAddDialog = { itemName ->
                viewModel.addItem(itemName)
                showAddDialog = false
            },
            onDismissDeleteDialog = { showDeleteDialog = false },
            onConfirmDeleteDialog = {
                viewModel.deleteItems(selectedItemIds)
                selectedItemIds = emptySet()
                showDeleteDialog = false
            }
        )
    }
}


@Composable
private fun ShoppingListDetailsTopBarHandler(
    selectedItemIds: Set<Int>,
    uiState: ShoppingListDetailsUiState,
    onNavigateBack: () -> Unit,
    onCancelSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    if (selectedItemIds.isEmpty()) {
        AppTopBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = uiState.listName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            navigationIcon = {
                ShoppingListDetailsBackButton(
                    contentDesc = stringResource(id = R.string.navigate_up),
                    onClick = onNavigateBack
                )
            }
        )
    } else {
        AppTopBar(
            title = {
                Text(
                    text = stringResource(id = R.string.selected_count, selectedItemIds.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                ShoppingListSelectionCancelButton(
                    contentDesc = stringResource(id = R.string.cancel),
                    onClick = onCancelSelection
                )
            },
            actions = {
                ShoppingListSelectionActions(
                    onSelectAll = onSelectAll,
                    onDeleteSelected = onDeleteSelected
                )
            }
        )
    }
}

@Composable
private fun ShoppingListDetailsDialogs(
    showAddDialog: Boolean,
    showDeleteDialog: Boolean,
    onDismissAddDialog: () -> Unit,
    onConfirmAddDialog: (String) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDeleteDialog: () -> Unit
) {
    if (showAddDialog) {
        AddItemDialog(
            onDismiss = onDismissAddDialog,
            onAdd = onConfirmAddDialog
        )
    }

    if (showDeleteDialog) {
        DeleteItemDialog(
            onDismiss = onDismissDeleteDialog,
            onConfirm = onConfirmDeleteDialog
        )
    }
}

@Composable
private fun ShoppingListDetailsHeader(uiState: ShoppingListDetailsUiState) {
    if (uiState.totalWithOrphansA > 0 || uiState.totalWithOrphansB > 0 || uiState.hasOrphans) {
        ShoppingListBadges(
            totalA = uiState.totalWithOrphansA,
            totalB = uiState.totalWithOrphansB,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShoppingListSavingsCard(
            totalA = uiState.totalWithOrphansA,
            totalB = uiState.totalWithOrphansB,
            smartTotal = uiState.smartTotal,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (uiState.hasOrphans) {
            Spacer(modifier = Modifier.height(16.dp))
            ShoppingListOrphanCard(
                totalWithOrphansA = uiState.totalWithOrphansA,
                totalWithOrphansB = uiState.totalWithOrphansB,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ShoppingListDetailsList(
    items: List<ShoppingListItemEntity>,
    isSearchActive: Boolean,
    selectedItemIds: Set<Int>,
    onToggleSelection: (Int, Boolean) -> Unit,
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit
) {
    if (items.isEmpty()) {
        ShoppingListDetailsEmptyState(isSearchActive = isSearchActive)
    } else {
        ShoppingListItemsColumn(
            items = items,
            selectedItemIds = selectedItemIds,
            onToggleSelection = onToggleSelection,
            onNavigateToCompare = onNavigateToCompare
        )
    }
}

@Composable
private fun ShoppingListItemsColumn(
    items: List<ShoppingListItemEntity>,
    selectedItemIds: Set<Int>,
    onToggleSelection: (Int, Boolean) -> Unit,
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            val isSelected = selectedItemIds.contains(item.id)
            ShoppingListItemCard(
                item = item,
                isSelected = isSelected,
                isSelectionMode = selectedItemIds.isNotEmpty(),
                onClick = {
                    if (selectedItemIds.isNotEmpty()) {
                        onToggleSelection(item.id, isSelected)
                    } else {
                        onNavigateToCompare(item)
                    }
                },
                onLongClick = {
                    onToggleSelection(item.id, isSelected)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListDetailsBackButton(contentDesc: String, onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = contentDesc,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListSelectionCancelButton(contentDesc: String, onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = contentDesc,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListSelectionActions(
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = stringResource(id = R.string.select_all),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onSelectAll) {
            Icon(
                imageVector = Icons.Default.SelectAll,
                contentDescription = stringResource(id = R.string.select_all),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onDeleteSelected) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
