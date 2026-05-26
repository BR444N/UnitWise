package com.br444n.unitwise.app.core.ui.components.inputs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.Badge

import androidx.compose.ui.tooling.preview.Preview
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class AppDropdownMenuConfig<T>(
    val selectedItem: T,
    val items: List<T>,
    val itemLabel: (T) -> String = { it.toString() },
    val isItemEnabled: (T) -> Boolean = { true },
    val isReadOnly: Boolean = false,
    val label: String? = null
)

data class AppDropdownMenuActions<T>(
    val onItemSelected: (T) -> Unit,
    val onDisabledItemClick: (T) -> Unit = {}
)

data class AppDropdownMenuFocusConfig(
    val focusRequester: FocusRequester? = null,
    val nextFocusRequester: FocusRequester? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdownMenu(
    config: AppDropdownMenuConfig<T>,
    actions: AppDropdownMenuActions<T>,
    modifier: Modifier = Modifier,
    focusConfig: AppDropdownMenuFocusConfig = AppDropdownMenuFocusConfig()
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && !config.isReadOnly,
        onExpandedChange = { if (!config.isReadOnly) expanded = !expanded },
        modifier = modifier
    ) {
        var fieldModifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        if (focusConfig.focusRequester != null) {
            fieldModifier = fieldModifier.focusRequester(focusConfig.focusRequester)
        }
        fieldModifier = fieldModifier.onFocusChanged {
            if (it.isFocused && !config.isReadOnly) {
                expanded = true
            }
        }

        AppTextField(
            value = config.itemLabel(config.selectedItem),
            onValueChange = {},
            modifier = fieldModifier,
            config = AppTextFieldConfig(
                readOnly = true,
                enabled = !config.isReadOnly
            ),
            content = AppTextFieldContent(
                label = config.label?.let { { Text(it) } },
                trailingIcon = { 
                    if (!config.isReadOnly) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
                    }
                }
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AppDropdownMenuItems(
                config = config,
                actions = actions,
                onDismissRequest = { expanded = false },
                nextFocusRequester = focusConfig.nextFocusRequester
            )
        }
    }
}

@Composable
private fun <T> AppDropdownMenuItems(
    config: AppDropdownMenuConfig<T>,
    actions: AppDropdownMenuActions<T>,
    onDismissRequest: () -> Unit,
    nextFocusRequester: FocusRequester?
) {
    config.items.forEachIndexed { index, item ->
        val isSelected = item == config.selectedItem
        val isEnabled = config.isItemEnabled(item)
        
        DropdownMenuItem(
            text = { 
                Text(
                    text = config.itemLabel(item),
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
                    actions.onItemSelected(item)
                    onDismissRequest()
                    nextFocusRequester?.requestFocus()
                } else {
                    actions.onDisabledItemClick(item)
                }
            },
            enabled = true
        )
        
        if (index < config.items.size - 1) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                color = Badge.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppDropdownMenuPreview() {
    UnitWiseTheme {
        AppDropdownMenu(
            config = AppDropdownMenuConfig(
                selectedItem = "Option 1",
                items = listOf("Option 1", "Option 2", "Option 3"),
                label = "Select an option"
            ),
            actions = AppDropdownMenuActions(
                onItemSelected = {}
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
