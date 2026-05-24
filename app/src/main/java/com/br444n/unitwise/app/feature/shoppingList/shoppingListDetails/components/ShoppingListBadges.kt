package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.utils.PriceUtils

@Composable
fun ShoppingListBadges(
    totalA: Double,
    totalB: Double,
    modifier: Modifier = Modifier
) {
    // Determine winner (lowest non-zero cost). If one is 0, it means no items compared yet for that list?
    // Usually if both are compared, they have a value. We pick the lowest.
    // If they are equal, we could highlight both or neither.
    val isAWinner = totalA > 0 && (totalA < totalB || totalB == 0.0)
    val isBWinner = totalB > 0 && (totalB < totalA || totalA == 0.0)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ListBadge(
            title = stringResource(R.string.list_a),
            total = totalA,
            isWinner = isAWinner,
            modifier = Modifier.weight(1f)
        )
        
        ListBadge(
            title = stringResource(R.string.list_b),
            total = totalB,
            isWinner = isBWinner,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ListBadge(
    title: String,
    total: Double,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        // Inner Container
        val borderModifier = if (isWinner) {
            Modifier.border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
        } else {
            Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
        }
        
        Column(
            modifier = borderModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = PriceUtils.formatPrice(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        if (isWinner) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 2.dp)
                )
                Text(
                    text = stringResource(R.string.winner_desc),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
