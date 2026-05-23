package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.utils.PriceUtils
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.BlueColor

@Composable
fun ShoppingListSavingsCard(
    totalA: Double,
    totalB: Double,
    smartTotal: Double,
    modifier: Modifier = Modifier
) {
    if (totalA == 0.0 && totalB == 0.0) return

    val winnerTotal = if (totalA < totalB) totalA else totalB
    val loserTotal = if (totalA > totalB) totalA else totalB
    val winnerName = if (totalA < totalB) stringResource(id = R.string.list_a) else stringResource(id = R.string.list_b)
    
    // Calculate savings by using the smart choices vs the worst case list
    val savings = if (smartTotal > 0 && smartTotal < loserTotal) loserTotal - smartTotal else loserTotal - winnerTotal
    val percentage = if (loserTotal > 0) (savings / loserTotal) * 100 else 0.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BlueColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = stringResource(id = R.string.total_list_a),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = PriceUtils.formatPrice(totalA),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.total_list_b),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = PriceUtils.formatPrice(totalB),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            if (savings > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Badge,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Savings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Total savings with $winnerName:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "${PriceUtils.formatPrice(savings)} (${String.format(java.util.Locale.getDefault(), "%.1f", percentage)}%)",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "No savings differences yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListSavingsCardPreview() {
    com.br444n.unitwise.app.ui.theme.UnitWiseTheme {
        ShoppingListSavingsCard(
            totalA = 24.50,
            totalB = 28.15,
            smartTotal = 20.00,
            modifier = Modifier.padding(16.dp)
        )
    }
}
