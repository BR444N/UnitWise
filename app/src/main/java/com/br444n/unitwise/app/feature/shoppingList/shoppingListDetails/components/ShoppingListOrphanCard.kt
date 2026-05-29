package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.utils.PriceUtils
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.BlueColor
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.core.ui.components.cards.AppBadgeCard

@Composable
fun ShoppingListOrphanCard(
    modifier: Modifier = Modifier,
    totalWithOrphansA: Double,
    totalWithOrphansB: Double,
    onToggle: (Boolean) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    AppBadgeCard(
        title = stringResource(id = R.string.orphan_items_impact),
        subtitle = "",
        icon = Icons.Default.Info,
        colors = com.br444n.unitwise.app.core.ui.components.cards.AppBadgeCardDefaults.colors(
            containerColor = Badge,
            borderColor = BrandPrimary,
            iconTint = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.clickable { 
            expanded = !expanded 
            onToggle(expanded)
        },
        trailingContent = {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(animationSpec = tween(300)),
            exit = fadeOut() + shrinkVertically(animationSpec = tween(300))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.orphan_items_explanation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = BlueColor
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.list_a_absolute_total),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BlueColor
                    )
                    Text(
                        text = PriceUtils.formatPrice(totalWithOrphansA),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BlueColor
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.list_b_absolute_total),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BlueColor
                    )
                    Text(
                        text = PriceUtils.formatPrice(totalWithOrphansB),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BlueColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListOrphanCardPreview() {
    UnitWiseTheme {
        ShoppingListOrphanCard(
            totalWithOrphansA = 150.50,
            totalWithOrphansB = 160.00,
            modifier = Modifier.padding(16.dp)
        )
    }
}
