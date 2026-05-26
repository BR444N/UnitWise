package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
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
import com.br444n.unitwise.app.core.ui.components.wrappers.AppHighlightedWrapper

@Composable
fun ShoppingListBadges(
    totalA: Double,
    totalB: Double,
    modifier: Modifier = Modifier
) {
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
    if (isWinner) {
        AppHighlightedWrapper(
            badgeText = stringResource(R.string.winner_desc),
            badgeIcon = Icons.Default.Stars,
            modifier = modifier
        ) {
            BadgeContent(title, total, isWinner)
        }
    } else {
        BadgeContent(title, total, isWinner, modifier)
    }
}

@Composable
private fun BadgeContent(
    title: String,
    total: Double,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (isWinner) {
        modifier
    } else {
        modifier.border(
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
}
