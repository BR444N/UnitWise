package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ShoppingListDetailsEmptyState(
    isSearchActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(bottom = 80.dp), // To account for FAB
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                id = if (isSearchActive) R.drawable.empty_products 
                     else R.drawable.empty_state_list_details
            ),
            contentDescription = null, // Decorative
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isSearchActive) stringResource(id = R.string.details_search_empty_title) 
                   else stringResource(id = R.string.details_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isSearchActive) stringResource(id = R.string.details_search_empty_subtitle) 
                   else stringResource(id = R.string.details_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListDetailsEmptyStatePreview() {
    UnitWiseTheme {
        ShoppingListDetailsEmptyState(isSearchActive = false)
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListDetailsEmptyStateSearchPreview() {
    UnitWiseTheme {
        ShoppingListDetailsEmptyState(isSearchActive = true)
    }
}
