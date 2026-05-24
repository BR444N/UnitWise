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
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.AddItemButton
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.AddItemDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.DeleteItemDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListBadges
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListItemCard
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListSavingsCard
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListDetailsTopAppBar
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListSelectionAppBar
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListSearchBar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
            AddItemButton(onClick = { showAddDialog = true })
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
        ShoppingListDetailsTopAppBar(
            title = uiState.listName,
            onNavigateBack = onNavigateBack
        )
    } else {
        ShoppingListSelectionAppBar(
            selectedCount = selectedItemIds.size,
            onCancelSelection = onCancelSelection,
            onSelectAll = onSelectAll,
            onDeleteSelected = onDeleteSelected
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
