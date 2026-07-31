package com.br444n.unitwise.app.core.ui.components.cards

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.buttons.AppPrimaryButton
import com.br444n.unitwise.app.core.ui.components.buttons.AppSecondaryButton
import com.br444n.unitwise.app.core.ui.components.feedback.UnitWiseTooltip
import com.br444n.unitwise.app.core.ui.components.lists.AppListDivider
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppComparisonCardConfig(
    val title: String,
    val timestamp: Long,
    val badgeText: String? = null,
    val primaryActionText: String,
    val secondaryActionText: String,
    val primaryActionIcon: ImageVector? = null,
    val secondaryActionIcon: ImageVector? = null,
)

data class AppComparisonCardActions(
    val onEditClick: () -> Unit = {},
    val onPrimaryActionClick: () -> Unit = {},
    val onSecondaryActionClick: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppComparisonCard(
    config: AppComparisonCardConfig,
    actions: AppComparisonCardActions,
    modifier: Modifier = Modifier,
) {
    val formattedDate =
        SimpleDateFormat(
            "MMM dd, yyyy • hh:mm a",
            Locale.getDefault(),
        ).format(Date(config.timestamp))

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = config.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    UnitWiseTooltip(
                        tooltipText = stringResource(id = R.string.edit),
                    ) {
                        IconButton(
                            onClick = actions.onEditClick,
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(id = R.string.edit),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))
                AppListDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppPrimaryButton(
                        text = config.primaryActionText,
                        onClick = actions.onPrimaryActionClick,
                        icon = config.primaryActionIcon,
                        modifier = Modifier.weight(1f),
                    )

                    AppSecondaryButton(
                        text = config.secondaryActionText,
                        onClick = actions.onSecondaryActionClick,
                        icon = config.secondaryActionIcon,
                    )
                }
            }
        }

        // Top-left overlay badge
        if (config.badgeText != null) {
            AppMicroBadge(
                text = config.badgeText,
                icon = Icons.Default.Verified,
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 12.dp, y = 0.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppComparisonCardPreview() {
    UnitWiseTheme {
        AppComparisonCard(
            config =
                AppComparisonCardConfig(
                    title = "Gel Neutrogena 600g vs Gel Nivea 400g",
                    timestamp = System.currentTimeMillis(),
                    badgeText = "Gel Neutrogena",
                    primaryActionText = "View Details",
                    secondaryActionText = "Share",
                    primaryActionIcon = Icons.AutoMirrored.Filled.ArrowForward,
                    secondaryActionIcon = Icons.Default.Share,
                ),
            actions =
                AppComparisonCardActions(
                    onEditClick = {},
                    onPrimaryActionClick = {},
                    onSecondaryActionClick = {},
                ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
