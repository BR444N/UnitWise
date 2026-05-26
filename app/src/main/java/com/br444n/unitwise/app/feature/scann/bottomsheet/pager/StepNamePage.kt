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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldKeyboard
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.R
import com.br444n.unitwise.app.domain.model.PRODUCT_NAME_MAX_LENGTH

@Composable
fun StepNamePage(
    productName: String,
    onNameChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.scann_step_name_guide),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppTextField(
            value = productName,
            onValueChange = { input ->
                onNameChanged(input.take(PRODUCT_NAME_MAX_LENGTH))
            },
            modifier = Modifier.fillMaxWidth(),
            keyboard = AppTextFieldKeyboard(
                options = KeyboardOptions(imeAction = ImeAction.Next)
            ),
            content = AppTextFieldContent(
                label = { Text(stringResource(id = R.string.product_name_label)) },
                placeholder = { Text(stringResource(id = R.string.scan_hint)) }
            )
        )
        if (productName.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.scann_step_name_edit_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
