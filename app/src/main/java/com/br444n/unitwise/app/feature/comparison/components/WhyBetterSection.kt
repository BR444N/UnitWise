package com.br444n.unitwise.app.feature.comparison.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun WhyBetterSection(
    modifier: Modifier = Modifier,
    savingsPerStandardUnit: String,
    standardUnitDesc: String,
    estimatedMonthlySavings: String,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.why_better_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        WhyBetterListItem(
            text = stringResource(
                id = R.string.why_better_saving_per_unit,
                savingsPerStandardUnit,
                standardUnitDesc
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        WhyBetterListItem(
            text = stringResource(
                id = R.string.why_better_monthly_saving,
                estimatedMonthlySavings
            )
        )
    }
}

@Composable
private fun WhyBetterListItem(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 2.dp) // Align icon with the first line of text
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WhyBetterSectionPreview() {
    UnitWiseTheme {
        WhyBetterSection(
            savingsPerStandardUnit = "1.50",
            standardUnitDesc = "100 g",
            estimatedMonthlySavings = "10.00",
            modifier = Modifier.padding(16.dp)
        )
    }
}
