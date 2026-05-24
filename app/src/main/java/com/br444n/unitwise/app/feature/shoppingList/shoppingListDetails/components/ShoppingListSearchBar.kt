package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ShoppingListSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp)),
        placeholder = {
            Text(
                text = stringResource(R.string.search_product_hint),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null, // decorative
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedBorderColor = Transparent,
            disabledBorderColor = Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(name = "Light Mode - Empty", showBackground = true)
@Preview(name = "Dark Mode - Empty", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ShoppingListSearchBarEmptyPreview() {
    UnitWiseTheme {
        ShoppingListSearchBar(
            query = "",
            onQueryChange = {}
        )
    }
}

@Preview(name = "Light Mode - With Text", showBackground = true)
@Composable
fun ShoppingListSearchBarFilledPreview() {
    UnitWiseTheme {
        ShoppingListSearchBar(
            query = "Supermercado",
            onQueryChange = {}
        )
    }
}
