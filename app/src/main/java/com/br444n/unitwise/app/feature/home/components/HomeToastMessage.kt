package com.br444n.unitwise.app.feature.home.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import kotlinx.coroutines.delay

private const val TOAST_DURATION_MS = 2000L

@Composable
fun HomeToastMessage(
    eventKey: Int,
    @StringRes messageResId: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(eventKey) {
        if (eventKey > 0) {
            visible = true
            delay(TOAST_DURATION_MS)
            visible = false
        }
    }

    HomeToastMessage(
        visible = visible,
        message = stringResource(messageResId),
        modifier = modifier
    )
}

@Composable
fun HomeToastMessage(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 6.dp
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeToastMessagePreview() {
    UnitWiseTheme {
        HomeToastMessage(
            visible = true,
            message = "Units cannot be compared.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
