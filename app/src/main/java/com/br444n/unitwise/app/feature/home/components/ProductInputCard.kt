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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.domain.model.MeasurementUnit
import com.br444n.unitwise.app.domain.model.MeasurementUnit.SUPPORTED_UNITS
import com.br444n.unitwise.app.feature.home.isValid
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.BrandPrimaryUnfocused
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import androidx.compose.material3.HorizontalDivider

private const val PRODUCT_NAME_MAX_LENGTH = 24
private const val CONTENT_AMOUNT_MAX_LENGTH = 7
private const val PRICE_MAX_LENGTH = 7
private const val QUANTITY_MAX_LENGTH = 3

data class ProductInputState(
    val productName: String = "",
    val contentAmount: String = "",
    val selectedUnit: String = "g",
    val price: String = "",
    val quantity: String = "1"
)

data class ProductInputHints(
    val productNameHint: Int = R.string.scan_hint,
    val contentAmountLabel: Int = R.string.content_label,
    val priceLabel: Int = R.string.price_label
)

data class ProductInputOptions(
    val cardModifier: Modifier = Modifier,
    val scanButtonModifier: Modifier = Modifier,
    val focusConfig: ProductInputFocusConfig? = null,
    val hints: ProductInputHints = ProductInputHints(),
    val isReadOnly: Boolean = false,
    val otherSelectedUnit: String? = null
)

data class ProductInputActions(
    val onProductNameChange: (String) -> Unit = {},
    val onContentAmountChange: (String) -> Unit = {},
    val onUnitChange: (String) -> Unit = {},
    val onIncompatibleUnitSelected: () -> Unit = {},
    val onPriceChange: (String) -> Unit = {},
    val onQuantityChange: (String) -> Unit = {},
    val onScanClick: () -> Unit = {}
)

data class ProductInputFocusConfig(
    val productName: FocusRequester,
    val contentAmount: FocusRequester,
    val unit: FocusRequester,
    val price: FocusRequester,
    val quantity: FocusRequester,
    val nextProductName: FocusRequester? = null
)

private data class ProductContentFocusConfig(
    val contentAmount: FocusRequester?,
    val unit: FocusRequester?,
    val price: FocusRequester?
)

private data class ProductUnitConfig(
    val selectedUnit: String,
    val otherSelectedUnit: String?
)

private data class ProductContentCallbacks(
    val onContentAmountChange: (String) -> Unit,
    val onUnitChange: (String) -> Unit,
    val onIncompatibleUnitSelected: () -> Unit,
    val onFocusChange: (Boolean) -> Unit
)

private data class ProductPriceQuantityFocusConfig(
    val price: FocusRequester?,
    val quantity: FocusRequester?,
    val nextProductName: FocusRequester?
)

@Composable
fun ProductInputCard(
    modifier: Modifier = Modifier,
    title: String,
    state: ProductInputState,
    actions: ProductInputActions,
    options: ProductInputOptions = ProductInputOptions()
) {
    var isFocused by remember { mutableStateOf(false) }
    val onFocusChange: (Boolean) -> Unit = { focused -> if (focused) isFocused = true }

    Card(
        modifier = options.cardModifier.then(modifier).fillMaxWidth(),
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
                state = state,
                actions = actions,
                onFocusChange = { isFocused = it },
                options = options
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProductContentRow(
                state = state,
                actions = actions,
                onFocusChange = onFocusChange,
                options = options
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProductPriceQuantityRow(
                state = state,
                actions = actions,
                onFocusChange = onFocusChange,
                options = options
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
    state: ProductInputState,
    actions: ProductInputActions,
    onFocusChange: (Boolean) -> Unit,
    options: ProductInputOptions
) {
    val productName = state.productName
    val onProductNameChange = actions.onProductNameChange
    val onScanClick = actions.onScanClick
    val focusConfig = options.focusConfig
    val placeholderResId = options.hints.productNameHint
    val isReadOnly = options.isReadOnly
    val scanButtonModifier = options.scanButtonModifier
    OutlinedTextField(
        value = productName,
        onValueChange = { onProductNameChange(sanitizeProductNameInput(it)) },
        modifier = Modifier
            .fillMaxWidth()
            .thenFocusRequester(focusConfig?.productName)
            .onFocusChanged { onFocusChange(it.isFocused) },
        readOnly = isReadOnly,
        enabled = !isReadOnly,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusConfig?.contentAmount?.requestFocus() }
        ),
        placeholder = { Text(stringResource(id = placeholderResId)) },
        trailingIcon = if (isReadOnly) null else {
            { ScanIconTooltip(onScanClick, modifier = scanButtonModifier) }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanIconTooltip(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        IconButton(
            onClick = onScanClick,
            modifier = modifier
        ) {
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
    state: ProductInputState,
    actions: ProductInputActions,
    onFocusChange: (Boolean) -> Unit,
    options: ProductInputOptions
) {
    val contentAmount = state.contentAmount
    val isReadOnly = options.isReadOnly
    val labelResId = options.hints.contentAmountLabel
    
    val unitConfig = ProductUnitConfig(
        selectedUnit = state.selectedUnit,
        otherSelectedUnit = options.otherSelectedUnit
    )
    
    val callbacks = ProductContentCallbacks(
        onContentAmountChange = actions.onContentAmountChange,
        onUnitChange = actions.onUnitChange,
        onIncompatibleUnitSelected = actions.onIncompatibleUnitSelected,
        onFocusChange = onFocusChange
    )
    
    val focusConfig = ProductContentFocusConfig(
        contentAmount = options.focusConfig?.contentAmount,
        unit = options.focusConfig?.unit,
        price = options.focusConfig?.price
    )
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = contentAmount,
            onValueChange = { callbacks.onContentAmountChange(sanitizeDecimalInput(it, CONTENT_AMOUNT_MAX_LENGTH)) },
            modifier = Modifier
                .weight(1f)
                .thenFocusRequester(focusConfig.contentAmount)
                .onFocusChanged { callbacks.onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    keyboardController?.hide()
                    focusConfig.unit?.requestFocus()
                }
            ),
            label = { Text(stringResource(id = labelResId)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        UnitSelectorField(
            selectedUnit = unitConfig.selectedUnit,
            otherSelectedUnit = unitConfig.otherSelectedUnit,
            onUnitChange = callbacks.onUnitChange,
            onIncompatibleUnitSelected = callbacks.onIncompatibleUnitSelected,
            focusRequester = focusConfig.unit,
            nextFocusRequester = focusConfig.price,
            isReadOnly = isReadOnly
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.UnitSelectorField(
    selectedUnit: String,
    otherSelectedUnit: String?,
    onUnitChange: (String) -> Unit,
    onIncompatibleUnitSelected: () -> Unit,
    focusRequester: FocusRequester?,
    nextFocusRequester: FocusRequester?,
    isReadOnly: Boolean
) {
    val expanded = remember { mutableStateOf(false) }
    val compatibleUnits = remember(otherSelectedUnit) {
        MeasurementUnit.compatibleUnitsFor(otherSelectedUnit)
    }

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
            modifier = Modifier
                .thenFocusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused && !isReadOnly) {
                        expanded.value = true
                    }
                }
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable),
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
                compatibleUnits = compatibleUnits,
                onIncompatibleUnitSelected = onIncompatibleUnitSelected,
                onUnitChange = {
                    onUnitChange(it)
                    expanded.value = false
                    nextFocusRequester?.requestFocus()
                }
            )
        }
    }
}

@Composable
private fun UnitMenuItems(
    selectedUnit: String,
    compatibleUnits: List<String>,
    onIncompatibleUnitSelected: () -> Unit,
    onUnitChange: (String) -> Unit
) {
    SUPPORTED_UNITS.forEachIndexed { index, selectionOption ->
        val isSelected = selectedUnit == selectionOption
        val isEnabled = isSelected || compatibleUnits.contains(selectionOption)
        
        DropdownMenuItem(
            text = { 
                Text(
                    text = selectionOption,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isEnabled -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    },
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ) 
            },
            onClick = {
                if (isEnabled) {
                    onUnitChange(selectionOption)
                } else {
                    onIncompatibleUnitSelected()
                }
            },
            enabled = true
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
    state: ProductInputState,
    actions: ProductInputActions,
    onFocusChange: (Boolean) -> Unit,
    options: ProductInputOptions
) {
    val price = state.price
    val onPriceChange = actions.onPriceChange
    val quantity = state.quantity
    val onQuantityChange = actions.onQuantityChange
    val labelResId = options.hints.priceLabel
    val isReadOnly = options.isReadOnly
    val focusConfig = ProductPriceQuantityFocusConfig(
        price = options.focusConfig?.price,
        quantity = options.focusConfig?.quantity,
        nextProductName = options.focusConfig?.nextProductName
    )
    val isQuantityZero = quantity.toIntOrNull() == 0
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = price,
            onValueChange = { onPriceChange(sanitizeDecimalInput(it, PRICE_MAX_LENGTH)) },
            modifier = Modifier
                .weight(1f)
                .thenFocusRequester(focusConfig.price)
                .onFocusChanged { onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusConfig.quantity?.requestFocus() }
            ),
            label = { Text(stringResource(id = labelResId)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = quantity,
            onValueChange = { onQuantityChange(sanitizeQuantityInput(it)) },
            modifier = Modifier
                .weight(1f)
                .thenFocusRequester(focusConfig.quantity)
                .onFocusChanged { onFocusChange(it.isFocused) },
            readOnly = isReadOnly,
            enabled = !isReadOnly,
            isError = isQuantityZero,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = if (focusConfig.nextProductName != null) ImeAction.Next else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusConfig.nextProductName?.requestFocus() },
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            label = { Text(stringResource(id = R.string.quantity_label)) },
            supportingText = {
                if (isQuantityZero) {
                    Text(text = stringResource(id = R.string.quantity_min_error))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

private fun sanitizeProductNameInput(input: String): String {
    return input.take(PRODUCT_NAME_MAX_LENGTH)
}

private fun sanitizeDecimalInput(input: String, maxLength: Int): String {
    val normalized = buildString(input.length) {
        var hasDecimalSeparator = false
        input.forEach { char ->
            when {
                char.isDigit() -> append(char)
                (char == '.' || char == ',') && !hasDecimalSeparator -> {
                    append('.')
                    hasDecimalSeparator = true
                }
            }
        }
    }
    return normalized.take(maxLength)
}

private fun sanitizeQuantityInput(input: String): String {
    return input.filter(Char::isDigit).take(QUANTITY_MAX_LENGTH)
}

private fun Modifier.thenFocusRequester(focusRequester: FocusRequester?): Modifier {
    return if (focusRequester != null) {
        focusRequester(focusRequester)
    } else {
        this
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
