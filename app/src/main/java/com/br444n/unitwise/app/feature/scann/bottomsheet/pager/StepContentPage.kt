package com.br444n.unitwise.app.feature.scann.bottomsheet.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenu
import com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenuActions
import com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenuConfig
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldKeyboard
import com.br444n.unitwise.app.domain.model.CONTENT_AMOUNT_MAX_LENGTH
import com.br444n.unitwise.app.domain.model.MeasurementUnit.SUPPORTED_UNITS

@Composable
fun StepContentPage(
    content: String,
    selectedUnit: String,
    compatibleUnits: List<String>,
    onContentChanged: (String) -> Unit,
    onUnitChanged: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.scann_step_content_guide),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppTextField(
                value = content,
                onValueChange = { input ->
                    val normalized =
                        buildString(input.length) {
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
                    onContentChanged(normalized.take(CONTENT_AMOUNT_MAX_LENGTH))
                },
                modifier = Modifier.weight(1f),
                keyboard =
                    AppTextFieldKeyboard(
                        options = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    ),
                content =
                    AppTextFieldContent(
                        label = { Text(stringResource(id = R.string.label_content)) },
                        placeholder = { Text(stringResource(id = R.string.content_label)) },
                    ),
            )

            AppDropdownMenu(
                config =
                    AppDropdownMenuConfig(
                        selectedItem = selectedUnit,
                        items = SUPPORTED_UNITS,
                        itemLabel = { it },
                        isItemEnabled = { it == selectedUnit || compatibleUnits.contains(it) },
                        label = stringResource(id = R.string.unit_label),
                    ),
                actions =
                    AppDropdownMenuActions(
                        onItemSelected = { onUnitChanged(it) },
                    ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
