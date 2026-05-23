package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.BrandPrimaryUnfocused
import com.br444n.unitwise.app.ui.theme.TextSecondary
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListItemCard(
    modifier: Modifier = Modifier,
    item: ShoppingListItemEntity,
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
) {
    // If we have a winner (or tie), the item has been compared.
    val isCompared = item.isProductAWinner != null || item.isTie == true

    val isOrphan = item.productAName.isNotBlank() || item.productBName.isNotBlank()
    val isExpandable = isCompared || isOrphan

    // Local state for expansion
    var isExpanded by remember { mutableStateOf(false) }

    val actualContainerColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = {
                    when {
                        isSelectionMode -> onClick()
                        isExpandable -> isExpanded = !isExpanded
                        else -> onClick()
                    }
                },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = actualContainerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val showExpanded = isExpanded && isExpandable
            if (showExpanded) {
                ExpandedCardContent(item, isCompared)
            } else {
                CollapsedCardContent(item, isExpandable, isCompared, isOrphan, onClick)
            }
        }
    }
}

@Composable
private fun ExpandedCardContent(
    item: ShoppingListItemEntity,
    isCompared: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.categoryName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Product A Row
    ExpandedProductRow(
        name = item.productAName.ifBlank { stringResource(R.string.product_a_title) },
        price = item.productAPrice.ifBlank { "$0.00" },
        isWinner = item.isProductAWinner == true,
        isTie = item.isTie == true
    )

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )

    // Product B Row
    ExpandedProductRow(
        name = item.productBName.ifBlank { stringResource(R.string.product_b_title) },
        price = item.productBPrice.ifBlank { "$0.00" },
        isWinner = item.isProductAWinner == false && isCompared,
        isTie = item.isTie == true
    )
}

@Composable
private fun CollapsedCardContent(
    item: ShoppingListItemEntity,
    isExpandable: Boolean,
    isCompared: Boolean,
    isOrphan: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CollapsedCardTitleSection(
            item = item,
            isExpandable = isExpandable,
            isCompared = isCompared,
            isOrphan = isOrphan,
            modifier = Modifier.weight(1f)
        )

        CollapsedCardTrailingSection(
            item = item,
            isCompared = isCompared,
            onClick = onClick
        )
    }
}

@Composable
private fun CollapsedCardTitleSection(
    item: ShoppingListItemEntity,
    isExpandable: Boolean,
    isCompared: Boolean,
    isOrphan: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = item.categoryName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isExpandable) {
            val displayName = item.productAName.ifBlank { item.productBName }
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isOrphan && !isCompared) {
                Text(
                    text = "Complete comparison (Optional)",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandPrimaryUnfocused,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.touch_to_compare),
                style = MaterialTheme.typography.titleMedium,
                color = BrandPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CollapsedCardTrailingSection(
    item: ShoppingListItemEntity,
    isCompared: Boolean,
    onClick: () -> Unit
) {
    if (isCompared) {
        Spacer(modifier = Modifier.width(16.dp))

        val isAWinner = item.isProductAWinner == true
        val isBWinner = item.isProductAWinner == false

        PriceDisplay(
            price = item.productAPrice,
            isWinner = isAWinner,
            isTie = item.isTie == true,
            winnerColor = MaterialTheme.colorScheme.primary,
            loserColor = TextSecondary
        )

        VerticalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        PriceDisplay(
            price = item.productBPrice,
            isWinner = isBWinner,
            isTie = item.isTie == true,
            winnerColor = MaterialTheme.colorScheme.primary,
            loserColor = TextSecondary
        )
    } else {
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Complete comparison",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ExpandedProductRow(
    name: String,
    price: String,
    isWinner: Boolean,
    isTie: Boolean
) {
    val textColor = if (isWinner || isTie) MaterialTheme.colorScheme.onSurface else TextSecondary
    val priceColor = if (isWinner) MaterialTheme.colorScheme.primary else TextSecondary
    val decoration = if (!isWinner && !isTie) TextDecoration.LineThrough else null
    val weight = if (isWinner) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isWinner) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Winner",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = price,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = weight,
            color = priceColor,
            textDecoration = decoration
        )
    }
}

@Composable
private fun PriceDisplay(
    price: String,
    isWinner: Boolean,
    isTie: Boolean,
    winnerColor: Color,
    loserColor: Color,
    modifier: Modifier = Modifier
) {
    val decoration = if (!isWinner && !isTie) TextDecoration.LineThrough else null
    val textColor = if (isWinner) winnerColor else loserColor

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        if (isWinner) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Winner",
                tint = winnerColor,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }
        Text(
            text = price,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textDecoration = decoration
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListItemCardPendingPreview() {
    UnitWiseTheme {
        ShoppingListItemCard(
            item = ShoppingListItemEntity(
                listId = 1,
                categoryName = "Leche"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListItemCardComparedPreview() {
    UnitWiseTheme {
        ShoppingListItemCard(
            item = ShoppingListItemEntity(
                listId = 1,
                categoryName = "Leche",
                productAName = "Leche Santa Clara 1L",
                productAPrice = "$25.00",
                productBName = "Leche Alpura 1.5L",
                productBPrice = "$35.00",
                isProductAWinner = true,
                isTie = false
            ),
            onClick = {}
        )
    }
}
