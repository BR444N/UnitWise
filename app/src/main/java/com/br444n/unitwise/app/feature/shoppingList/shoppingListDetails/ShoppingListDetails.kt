package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity
import com.br444n.unitwise.app.core.ui.components.buttons.AppFloatingActionButton
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.AddItemDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog.DeleteItemsDialog
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListBadges
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListItemCard
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListSavingsCard
import com.br444n.unitwise.app.core.ui.components.feedback.AppShowcaseOverlay
import com.br444n.unitwise.app.core.ui.components.feedback.AppShowcaseConfig
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import com.br444n.unitwise.app.core.ui.components.feedback.UnitWiseTooltip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import com.br444n.unitwise.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.res.stringResource
import com.br444n.unitwise.app.core.ui.components.states.AppEmptyState
import com.br444n.unitwise.app.core.ui.components.inputs.AppSearchBar
import com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components.ShoppingListOrphanCard

enum class ShoppingListShowcaseStep {
    ADD_PRODUCT,
    DELETE_ITEM,
    ORPHAN,
    NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit,
    viewModel: ShoppingListDetailsViewModel = viewModel(factory = ShoppingListDetailsViewModel.Factory)
) {
    val (showAddDialog, setShowAddDialog) = remember { mutableStateOf(false) }
    val (showDeleteDialog, setShowDeleteDialog) = remember { mutableStateOf(false) }
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
    val (selectedItemIds, setSelectedItemIds) = remember { mutableStateOf(emptySet<Int>()) }
    val uiState by viewModel.uiState.collectAsState()
    val seenFeatures by viewModel.seenFeatures.collectAsState()
    
    val (firstItemCoordinates, setFirstItemCoordinates) = remember { mutableStateOf<LayoutCoordinates?>(null) }
    val (orphanCardCoordinates, setOrphanCardCoordinates) = remember { mutableStateOf<LayoutCoordinates?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ShoppingListDetailsTopBarHandler(
                selectedItemIds = selectedItemIds,
                uiState = uiState,
                onNavigateBack = onNavigateBack,
                onCancelSelection = { setSelectedItemIds(emptySet()) },
                onSelectAll = { setSelectedItemIds(uiState.items.map { it.id }.toSet()) },
                onDeleteSelected = { setShowDeleteDialog(true) }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                text = stringResource(id = R.string.add_category_button),
                icon = Icons.Default.Add,
                onClick = { setShowAddDialog(true) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AppSearchBar(
                query = searchQuery,
                onQueryChange = { setSearchQuery(it) },
                hint = stringResource(R.string.search_product_hint)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoppingListDetailsHeader(
                uiState = uiState,
                onOrphanCardToggled = viewModel::onOrphanCardToggled,
                onOrphanCardPositioned = { setOrphanCardCoordinates(it) }
            )

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
                    setSelectedItemIds(if (isSelected) {
                        selectedItemIds - itemId
                    } else {
                        selectedItemIds + itemId
                    })
                },
                onNavigateToCompare = onNavigateToCompare,
                onFirstItemPositioned = { setFirstItemCoordinates(it) }
            )
        }

        ShoppingListDetailsDialogs(
            showAddDialog = showAddDialog,
            showDeleteDialog = showDeleteDialog,
            onDismissAddDialog = { setShowAddDialog(false) },
            onConfirmAddDialog = { itemName ->
                viewModel.addItem(itemName)
                setShowAddDialog(false)
            },
            onDismissDeleteDialog = { setShowDeleteDialog(false) },
            onConfirmDeleteDialog = {
                viewModel.deleteItems(selectedItemIds)
                setSelectedItemIds(emptySet())
                setShowDeleteDialog(false)
            }
        )
        
        ShoppingListShowcaseOverlayHandler(
            uiState = uiState,
            seenFeatures = seenFeatures,
            firstItemCoordinates = firstItemCoordinates,
            orphanCardCoordinates = orphanCardCoordinates,
            onMarkAsSeen = viewModel::markFeatureAsSeen
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
        DeleteItemsDialog(
            onDismissRequest = onDismissDeleteDialog,
            onConfirmClick = onConfirmDeleteDialog
        )
    }
}

@Composable
private fun ShoppingListDetailsHeader(
    uiState: ShoppingListDetailsUiState,
    onOrphanCardToggled: (Boolean) -> Unit,
    onOrphanCardPositioned: (LayoutCoordinates) -> Unit
) {
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
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .onGloballyPositioned { onOrphanCardPositioned(it) },
                onToggle = onOrphanCardToggled
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
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit,
    onFirstItemPositioned: (LayoutCoordinates) -> Unit
) {
    if (items.isEmpty()) {
        AppEmptyState(
            title = if (isSearchActive) stringResource(id = R.string.details_search_empty_title) 
                   else stringResource(id = R.string.details_empty_title),
            subtitle = if (isSearchActive) stringResource(id = R.string.details_search_empty_subtitle) 
                   else stringResource(id = R.string.details_empty_subtitle),
            iconResId = if (isSearchActive) R.drawable.empty_products 
                   else R.drawable.empty_state_list_details
        )
    } else {
        ShoppingListItemsColumn(
            items = items,
            selectedItemIds = selectedItemIds,
            onToggleSelection = onToggleSelection,
            onNavigateToCompare = onNavigateToCompare,
            onFirstItemPositioned = onFirstItemPositioned
        )
    }
}

@Composable
private fun ShoppingListItemsColumn(
    items: List<ShoppingListItemEntity>,
    selectedItemIds: Set<Int>,
    onToggleSelection: (Int, Boolean) -> Unit,
    onNavigateToCompare: (ShoppingListItemEntity) -> Unit,
    onFirstItemPositioned: (LayoutCoordinates) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size, key = { items[it].id }) { index ->
            val item = items[index]
            val isSelected = selectedItemIds.contains(item.id)
            Box(
                modifier = if (index == 0) Modifier.onGloballyPositioned { onFirstItemPositioned(it) } else Modifier
            ) {
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
}

@Composable
private fun ShoppingListDetailsBackButton(contentDesc: String, onClick: () -> Unit) {
    UnitWiseTooltip(
        tooltipText = contentDesc
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

@Composable
private fun ShoppingListSelectionCancelButton(contentDesc: String, onClick: () -> Unit) {
    UnitWiseTooltip(
        tooltipText = contentDesc
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

@Composable
private fun ShoppingListSelectionActions(
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    UnitWiseTooltip(
        tooltipText = stringResource(id = R.string.select_all)
    ) {
        IconButton(onClick = onSelectAll) {
            Icon(
                imageVector = Icons.Default.SelectAll,
                contentDescription = stringResource(id = R.string.select_all),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    UnitWiseTooltip(
        tooltipText = stringResource(id = R.string.delete),
        isError = true
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

@Composable
private fun ShoppingListShowcaseOverlayHandler(
    uiState: ShoppingListDetailsUiState,
    seenFeatures: Set<String>,
    firstItemCoordinates: LayoutCoordinates?,
    orphanCardCoordinates: LayoutCoordinates?,
    onMarkAsSeen: (String) -> Unit
) {
    val currentStep = remember(uiState, seenFeatures, firstItemCoordinates, orphanCardCoordinates) {
        when {
            uiState.items.size == 1 &&
            !seenFeatures.contains("feature_shopping_list_add_product") &&
            firstItemCoordinates != null -> ShoppingListShowcaseStep.ADD_PRODUCT

            uiState.items.isNotEmpty() &&
            !seenFeatures.contains("feature_shopping_list_delete_item") &&
            firstItemCoordinates != null -> ShoppingListShowcaseStep.DELETE_ITEM

            uiState.hasOrphans &&
            !seenFeatures.contains("feature_shopping_list_orphan") &&
            orphanCardCoordinates != null -> ShoppingListShowcaseStep.ORPHAN

            else -> ShoppingListShowcaseStep.NONE
        }
    }

    if (currentStep == ShoppingListShowcaseStep.NONE) return

    val (config, targetCoordinates, onDismiss) = when (currentStep) {
        ShoppingListShowcaseStep.ADD_PRODUCT -> Triple(
            AppShowcaseConfig(
                titleRes = R.string.showcase_add_product_title,
                bodyRes = R.string.showcase_add_product_desc,
                actionRes = R.string.home_showcase_next,
                dialogAlignment = Alignment.Center
            ),
            firstItemCoordinates
        ) { onMarkAsSeen("feature_shopping_list_add_product") }
        ShoppingListShowcaseStep.DELETE_ITEM -> Triple(
            AppShowcaseConfig(
                titleRes = R.string.showcase_delete_item_title,
                bodyRes = R.string.showcase_delete_item_desc,
                actionRes = R.string.home_showcase_next,
                dialogAlignment = Alignment.Center
            ),
            firstItemCoordinates
        ) { onMarkAsSeen("feature_shopping_list_delete_item") }
        ShoppingListShowcaseStep.ORPHAN -> Triple(
            AppShowcaseConfig(
                titleRes = R.string.showcase_orphan_title,
                bodyRes = R.string.showcase_orphan_desc,
                actionRes = R.string.home_showcase_finish,
                dialogAlignment = Alignment.TopCenter,
                topPadding = 180.dp
            ),
            orphanCardCoordinates
        ) { onMarkAsSeen("feature_shopping_list_orphan") }
        else -> return
    }

    AppShowcaseOverlay(
        targetCoordinates = targetCoordinates,
        config = config,
        onNext = onDismiss,
        onSkip = onDismiss
    )
}
