package com.br444n.unitwise.app.feature.shoppingList.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.buttons.AppPrimaryButton
import com.br444n.unitwise.app.core.ui.components.buttons.AppSecondaryButton
import com.br444n.unitwise.app.core.ui.components.dialogs.AppDialog

@Composable
fun DeleteListsDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    AppDialog(onDismissRequest = onDismissRequest) {
        Text(
            text = stringResource(id = R.string.delete_lists_dialog_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.delete_lists_dialog_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            AppSecondaryButton(
                text = stringResource(id = R.string.cancel),
                onClick = onDismissRequest,
            )
            Spacer(modifier = Modifier.width(8.dp))
            AppPrimaryButton(
                text = stringResource(id = R.string.delete),
                onClick = onConfirmClick,
                containerColor = MaterialTheme.colorScheme.error,
            )
        }
    }
}
