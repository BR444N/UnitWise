package com.br444n.unitwise.app.core.ui.components.inputs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class AppTextFieldConfig(
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val singleLine: Boolean = true,
    val maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val isError: Boolean = false
)

data class AppTextFieldContent(
    val label: @Composable (() -> Unit)? = null,
    val placeholder: @Composable (() -> Unit)? = null,
    val supportingText: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
    val leadingIcon: @Composable (() -> Unit)? = null
)

data class AppTextFieldKeyboard(
    val options: KeyboardOptions = KeyboardOptions.Default,
    val actions: KeyboardActions = KeyboardActions.Default
)

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    config: AppTextFieldConfig = AppTextFieldConfig(),
    content: AppTextFieldContent = AppTextFieldContent(),
    keyboard: AppTextFieldKeyboard = AppTextFieldKeyboard()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = config.enabled,
        readOnly = config.readOnly,
        label = content.label,
        placeholder = content.placeholder,
        trailingIcon = content.trailingIcon,
        leadingIcon = content.leadingIcon,
        supportingText = content.supportingText,
        isError = config.isError,
        keyboardOptions = keyboard.options,
        keyboardActions = keyboard.actions,
        singleLine = config.singleLine,
        maxLines = config.maxLines,
        minLines = config.minLines,
        shape = RoundedCornerShape(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AppTextFieldPreview() {
    UnitWiseTheme {
        AppTextField(
            value = "",
            onValueChange = {},
            content = AppTextFieldContent(
                label = { Text("Label") },
                placeholder = { Text("Placeholder") }
            )
        )
    }
}
