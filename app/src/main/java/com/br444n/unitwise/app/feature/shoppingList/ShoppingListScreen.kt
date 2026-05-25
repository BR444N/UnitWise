package com.br444n.unitwise.app.feature.shoppingList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListTopAppBar
import com.br444n.unitwise.app.feature.shoppingList.dialog.DeleteListDialog
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListSelectionAppBar
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListEmptyState
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListCard
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListCardState
import com.br444n.unitwise.app.feature.shoppingList.components.AddListButton
import com.br444n.unitwise.app.feature.shoppingList.dialog.CreateListDialog
import com.br444n.unitwise.app.data.local.dao.ShoppingListWithItemCount

@Composable
fun ShoppingListScreen(
    onNavigate: (Int) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    viewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModel.Factory)
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedListIds by remember { mutableStateOf(emptySet<Int>()) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (selectedListIds.isEmpty()) {
                ShoppingListTopAppBar()
            } else {
                ShoppingListSelectionAppBar(
                    selectedCount = selectedListIds.size,
                    onCancelSelection = { selectedListIds = emptySet() },
                    onSelectAll = { selectedListIds = uiState.lists.map { it.list.id }.toSet() },
                    onDeleteSelected = {
                        showDeleteDialog = true
                    }
                )
            }
        },
        bottomBar = {
            UnitWiseBottomNavigation(
                selectedIndex = 1,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            if (uiState.lists.isNotEmpty()) {
                AddListButton(
                    onClick = { showCreateDialog = true }
                )
            }
        }
    ) { innerPadding ->
        ShoppingListContent(
            isLoading = uiState.isLoading,
            lists = uiState.lists,
            selectedListIds = selectedListIds,
            onToggleSelection = { id, isSelected ->
                selectedListIds = if (isSelected) selectedListIds - id else selectedListIds + id
            },
            onNavigateToDetails = onNavigateToDetails,
            onCreateListClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

        if (showCreateDialog) {
            CreateListDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, colorArgb ->
                    viewModel.createList(name, colorArgb) { newListId ->
                        showCreateDialog = false
                        onNavigateToDetails(newListId)
                    }
                }
            )
        }

        if (showDeleteDialog) {
            DeleteListDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteLists(selectedListIds)
                    selectedListIds = emptySet()
                    showDeleteDialog = false
                }
            )
        }
    }
}

@Composable
private fun ShoppingListContent(
    isLoading: Boolean,
    lists: List<ShoppingListWithItemCount>,
    selectedListIds: Set<Int>,
    onToggleSelection: (Int, Boolean) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    onCreateListClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            // Keep background clean while loading to avoid flickers
        }

        lists.isEmpty() -> {
            Box(modifier = modifier) {
                ShoppingListEmptyState(
                    onCreateListClick = onCreateListClick
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lists, key = { it.list.id }) { item ->
                    val isSelected = selectedListIds.contains(item.list.id)
                    ShoppingListCard(
                        state = ShoppingListCardState(
                            name = item.list.name,
                            timestamp = item.list.timestamp,
                            productCount = item.itemCount,
                            colorArgb = item.list.colorBadge,
                            isSelected = isSelected
                        ),
                        onClick = {
                            if (selectedListIds.isNotEmpty()) {
                                onToggleSelection(item.list.id, isSelected)
                            } else {
                                onNavigateToDetails(item.list.id)
                            }
                        },
                        onLongClick = {
                            onToggleSelection(item.list.id, isSelected)
                        }
                    )
                }
            }
        }
    }
}
