package com.br444n.unitwise.app.feature.shoppingList.shoppingListDetails.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.core.ui.components.dialogs.AppDialog
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldKeyboard
import com.br444n.unitwise.app.core.ui.components.buttons.AppPrimaryButton
import com.br444n.unitwise.app.core.ui.components.buttons.AppSecondaryButton

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AppDialog(onDismissRequest = onDismiss) {
        Text(
            text = stringResource(id = R.string.add_category_dialog_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = itemName,
            onValueChange = { if (it.length <= 40) itemName = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            content = AppTextFieldContent(
                label = { Text(stringResource(id = R.string.category_name_hint)) }
            ),
            keyboard = AppTextFieldKeyboard(
                options = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            AppSecondaryButton(
                text = stringResource(id = R.string.cancel),
                onClick = onDismiss
            )
            Spacer(modifier = Modifier.width(8.dp))
            AppPrimaryButton(
                text = stringResource(id = R.string.add_category_button),
                onClick = { onAdd(itemName.trim()) },
                enabled = itemName.isNotBlank()
            )
        }
    }
}

@Preview
@Composable
fun AddItemDialogPreview() {
    UnitWiseTheme {
        AddItemDialog(
            onDismiss = {},
            onAdd = {}
        )
    }
}
