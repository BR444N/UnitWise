package com.br444n.unitwise.app.feature.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val HISTORY_TITLE_WRAP_THRESHOLD = 20

data class HistoryComparisonCardActions(
    val onEditClick: () -> Unit = {},
    val onViewDetailsClick: () -> Unit = {},
    val onShareClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryComparisonCard(
    modifier: Modifier = Modifier,
    productAName: String,
    productBName: String,
    winnerName: String?,
    timestamp: Long,
    actions: HistoryComparisonCardActions = HistoryComparisonCardActions()
) {
    val formattedDate = SimpleDateFormat(
        "MMM dd, yyyy • hh:mm a",
        Locale.getDefault()
    ).format(Date(timestamp))

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp), // space for the badge overlay
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = formatComparisonTitle(
                            productAName = productAName,
                            productBName = productBName,
                            defaultProductA = stringResource(R.string.comparison_default_product_a),
                            defaultProductB = stringResource(R.string.comparison_default_product_b)
                        ),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
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
                                    text = stringResource(id = R.string.edit),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(
                            onClick = actions.onEditClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.edit),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // View Details (primary, green)
                    Button(
                        onClick = actions.onViewDetailsClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.view_details),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Share (outlined / secondary tone)
                    OutlinedButton(
                        onClick = actions.onShareClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(id = R.string.share),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.share),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Top-left overlay badge
        BestValueMicroBadge(
            winnerName = winnerName,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 12.dp, y = 0.dp)
        )
    }
}

private fun formatComparisonTitle(
    productAName: String,
    productBName: String,
    defaultProductA: String,
    defaultProductB: String
): String {
    val firstName = productAName.ifBlank { defaultProductA }
    val secondName = productBName.ifBlank { defaultProductB }

    return if (firstName.length > HISTORY_TITLE_WRAP_THRESHOLD || secondName.length > HISTORY_TITLE_WRAP_THRESHOLD) {
        "$firstName vs\n$secondName"
    } else {
        "$firstName vs $secondName"
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryComparisonCardPreview() {
    UnitWiseTheme {
        HistoryComparisonCard(
            productAName = "Gel Neutrogena 600g",
            productBName = "Gel Nivea 400g",
            winnerName = "Gel Neutrogena",
            timestamp = System.currentTimeMillis(),
            actions = HistoryComparisonCardActions(),
            modifier = Modifier.padding(16.dp)
        )
    }
}
