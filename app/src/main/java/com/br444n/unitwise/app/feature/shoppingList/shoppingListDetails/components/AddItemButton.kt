package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun AddItemButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        icon = { 
            Icon(
                imageVector = Icons.Default.Add, 
                contentDescription = null 
            ) 
        },
        text = { 
            Text(
                text = stringResource(id = R.string.add_category_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            ) 
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Preview(showBackground = true)
@Composable
fun AddItemButtonPreview() {
    UnitWiseTheme {
        AddItemButton(
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}
