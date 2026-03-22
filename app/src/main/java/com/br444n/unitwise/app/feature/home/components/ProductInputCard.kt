package com.br444n.unitwise.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.domain.model.MeasurementUnit.SUPPORTED_UNITS
import com.br444n.unitwise.app.feature.home.isValid
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.BrandPrimaryUnfocused
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import androidx.compose.material3.HorizontalDivider


data class ProductInputState(
    val productName: String = "",
    val contentAmount: String = "",
    val selectedUnit: String = "g",
    val price: String = "",
    val quantity: String = "1"
)

data class ProductInputActions(
    val onProductNameChange: (String) -> Unit = {},
    val onContentAmountChange: (String) -> Unit = {},
    val onUnitChange: (String) -> Unit = {},
    val onPriceChange: (String) -> Unit = {},
    val onQuantityChange: (String) -> Unit = {},
    val onScanClick: () -> Unit = {}
)

@Composable
fun ProductInputCard(
    modifier: Modifier = Modifier,
    title: String,
    state: ProductInputState,
    actions: ProductInputActions,
    isReadOnly: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    val onFocusChange: (Boolean) -> Unit = { focused -> if (focused) isFocused = true }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProductCardHeader(title = title, isFocused = isFocused, isValid = state.isValid())
            Spacer(modifier = Modifier.height(16.dp))
            
            ProductNameField(
                productName = state.productName,
                onProductNameChange = actions.onProductNameChange,
                onScanClick = actions.onScanClick,
                onFocusChange = { isFocused = it },
                isReadOnly = isReadOnly
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProductContentRow(
                contentAmount = state.contentAmount,
                onContentAmountChange = actions.onContentAmountChange,
                selectedUnit = state.selectedUnit,
                onUnitChange = actions.onUnitChange,
                onFocusChange = onFocusChange,
                isReadOnly = isReadOnly
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProductPriceQuantityRow(
                price = state.price,
                onPriceChange = actions.onPriceChange,
                quantity = state.quantity,
                onQuantityChange = actions.onQuantityChange,
                onFocusChange = onFocusChange,
                isReadOnly = isReadOnly
            )
        }
    }
}

@Composable
private fun ProductCardHeader(title: String, isFocused: Boolean, isValid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    when {
                        isValid -> MaterialTheme.colorScheme.primary
                        isFocused -> BrandPrimaryUnfocused
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductNameField(
    productName: String,
    onProductNameChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    isReadOnly: Boolean
) {
    OutlinedTextField(
        value = productName,
        onValueChange = { if (it.length <= 200) onProductNameChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChange(it.isFocused) },
        readOnly = isReadOnly,
        enabled = !isReadOnly,
        placeholder = { Text(stringResource(id = R.string.scan_hint)) },
        trailingIcon = if (isReadOnly) null else {
            { ScanIconTooltip(onScanClick) }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanIconTooltip(onScanClick: () -> Unit) {
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
                    text = stringResource(id = R.string.scan_desc),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onScanClick) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = stringResource(id = R.string.scan_desc),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductContentRow(
    contentAmount: String,
    onContentAmountChange: (String) -> Unit,
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    isReadOnly: Boolean
) {

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = contentAmount,
            onValueChange = onContentAmountChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(id = R.string.content_label)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        UnitSelectorField(
            selectedUnit = selectedUnit,
            onUnitChange = onUnitChange,
            isReadOnly = isReadOnly
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.UnitSelectorField(
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    isReadOnly: Boolean
) {
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value && !isReadOnly,
        onExpandedChange = { if (!isReadOnly) expanded.value = !expanded.value },
        modifier = Modifier.weight(1f)
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            enabled = !isReadOnly,
            trailingIcon = { 
                if (!isReadOnly) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) 
                }
            },
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            label = { Text(stringResource(id = R.string.unit_label)) },
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            UnitMenuItems(
                selectedUnit = selectedUnit,
                onUnitChange = {
                    onUnitChange(it)
                    expanded.value = false
                }
            )
        }
    }
}

@Composable
private fun UnitMenuItems(
    selectedUnit: String,
    onUnitChange: (String) -> Unit
) {
    SUPPORTED_UNITS.forEachIndexed { index, selectionOption ->
        val isSelected = selectedUnit == selectionOption
        
        DropdownMenuItem(
            text = { 
                Text(
                    text = selectionOption,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            onClick = { onUnitChange(selectionOption) }
        )
        
        if (index < SUPPORTED_UNITS.size - 1) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Badge.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
private fun ProductPriceQuantityRow(
    price: String,
    onPriceChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    isReadOnly: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(id = R.string.price_label)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = quantity,
            onValueChange = onQuantityChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(id = R.string.quantity_label)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductInputCardPreview() {
    UnitWiseTheme {
        ProductInputCard(
            title = "Producto A",
            state = ProductInputState(),
            actions = ProductInputActions(),
            modifier = Modifier.padding(16.dp)
        )
    }
}
