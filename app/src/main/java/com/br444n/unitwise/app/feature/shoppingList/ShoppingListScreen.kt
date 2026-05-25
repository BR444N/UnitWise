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
import com.br444n.unitwise.app.navigation.components.UnitWiseBottomNavigation
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.shoppingList.components.ShoppingListEmptyState
import com.br444n.unitwise.app.feature.shoppingList.dialog.DeleteListDialog
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
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = null, // decorative
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.my_lists_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            } else {
                AppTopBar(
                    title = {
                        Text(
                            text = stringResource(
                                id = R.string.selected_count,
                                selectedListIds.size
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        ShoppingListSelectionCancelButton(
                            contentDesc = stringResource(id = R.string.cancel),
                            onClick = { selectedListIds = emptySet() }
                        )
                    },
                    actions = {
                        ShoppingListSelectionActions(
                            onSelectAll = {
                                selectedListIds = uiState.lists.map { it.list.id }.toSet()
                            },
                            onDeleteSelected = { showDeleteDialog = true }
                        )
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
