package com.br444n.unitwise.app.feature.scann.bottomsheet.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.domain.model.MeasurementUnit.SUPPORTED_UNITS
import com.br444n.unitwise.app.feature.scann.CONTENT_AMOUNT_MAX_LENGTH

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepContentPage(
    content: String,
    selectedUnit: String,
    compatibleUnits: List<String>,
    onContentChanged: (String) -> Unit,
    onUnitChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.scann_step_content_guide),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ContentAmountField(
                content = content,
                onContentChanged = onContentChanged
            )
            UnitDropdownField(
                expanded = expanded,
                selectedUnit = selectedUnit,
                compatibleUnits = compatibleUnits,
                onExpandedChange = { expanded = !expanded },
                onDismiss = { expanded = false },
                onFocus = { expanded = true },
                onUnitChanged = {
                    onUnitChanged(it)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun RowScope.ContentAmountField(
    content: String,
    onContentChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = content,
        onValueChange = { value -> onContentChanged(sanitizeContentAmount(value)) },
        modifier = Modifier.weight(1f),
        label = { Text(stringResource(id = R.string.content_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.UnitDropdownField(
    expanded: Boolean,
    selectedUnit: String,
    compatibleUnits: List<String>,
    onExpandedChange: () -> Unit,
    onDismiss: () -> Unit,
    onFocus: () -> Unit,
    onUnitChanged: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange() },
        modifier = Modifier.weight(1f)
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.unit_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .onFocusChanged { if (it.isFocused) onFocus() }
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss
        ) {
            SUPPORTED_UNITS.forEach { unit ->
                UnitDropdownItem(
                    unit = unit,
                    selectedUnit = selectedUnit,
                    compatibleUnits = compatibleUnits,
                    onUnitChanged = onUnitChanged
                )
            }
        }
    }
}

@Composable
private fun UnitDropdownItem(
    unit: String,
    selectedUnit: String,
    compatibleUnits: List<String>,
    onUnitChanged: (String) -> Unit
) {
    val isEnabled = unit in compatibleUnits || unit == selectedUnit
    DropdownMenuItem(
        text = {
            Text(
                text = unit,
                color = when {
                    unit == selectedUnit -> MaterialTheme.colorScheme.primary
                    isEnabled -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                }
            )
        },
        onClick = {
            if (isEnabled) {
                onUnitChanged(unit)
            }
        },
        enabled = true
    )
}

private fun sanitizeContentAmount(value: String): String {
    val normalized = value.filterIndexed { index, char ->
        char.isDigit() || ((char == '.' || char == ',') && index > 0)
    }
    return normalized.take(CONTENT_AMOUNT_MAX_LENGTH)
}
