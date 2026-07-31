package com.br444n.unitwise.app.feature.scann.bottomsheet.pager

import androidx.compose.foundation.layout.Column
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
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldKeyboard
import com.br444n.unitwise.app.domain.model.PRICE_MAX_LENGTH

@Composable
fun StepPricePage(
    price: String,
    onPriceChanged: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(id = R.string.scann_step_price_guide),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = price,
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
                onPriceChanged(normalized.take(PRICE_MAX_LENGTH))
            },
            modifier = Modifier.fillMaxWidth(),
            keyboard =
                AppTextFieldKeyboard(
                    options = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                ),
            content =
                AppTextFieldContent(
                    label = { Text(stringResource(id = R.string.label_price)) },
                    leadingIcon = { Text("$ ") },
                    placeholder = { Text(stringResource(id = R.string.price_label)) },
                ),
        )
    }
}
