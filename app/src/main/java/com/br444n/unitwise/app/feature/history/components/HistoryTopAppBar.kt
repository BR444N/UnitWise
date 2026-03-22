package com.br444n.unitwise.app.feature.history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.components.topAppBarBorder
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopAppBar(
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .topAppBarBorder(
                color = MaterialTheme.colorScheme.outline,
                width = 1.dp,
                cornerRadius = 24.dp
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Icon-only badge + title text beside it
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Badge: only the icon inside
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Badge)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null, // decorative
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Title text beside the badge
            Text(
                text = stringResource(id = R.string.history_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Right side: More Actions + Tooltip
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
                        text = stringResource(id = R.string.more_options),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            state = rememberTooltipState()
        ) {
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryTopAppBarPreview() {
    UnitWiseTheme {
        HistoryTopAppBar()
    }
}
