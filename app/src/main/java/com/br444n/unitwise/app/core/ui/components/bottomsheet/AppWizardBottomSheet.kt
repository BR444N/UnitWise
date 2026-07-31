package com.br444n.unitwise.app.core.ui.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.app.core.ui.components.buttons.AppPrimaryButton
import com.br444n.unitwise.app.core.ui.components.buttons.AppSecondaryButton
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

data class AppWizardBottomSheetConfig(
    val title: String,
    val progress: Float,
    val pageCount: Int,
    val currentPageIndex: Int,
    val primaryButtonText: String,
    val isPrimaryEnabled: Boolean = true,
    val secondaryButtonText: String? = null,
)

data class AppWizardBottomSheetActions(
    val onPageChanged: (Int) -> Unit,
    val onPrimaryClick: () -> Unit,
    val onSecondaryClick: (() -> Unit)? = null,
)

@Composable
fun AppWizardBottomSheet(
    config: AppWizardBottomSheetConfig,
    actions: AppWizardBottomSheetActions,
    modifier: Modifier = Modifier,
    pagerContent: @Composable (page: Int) -> Unit,
) {
    val pagerState =
        rememberPagerState(
            initialPage = config.currentPageIndex,
        ) { config.pageCount }

    LaunchedEffect(config.currentPageIndex) {
        if (pagerState.currentPage != config.currentPageIndex) {
            pagerState.animateScrollToPage(config.currentPageIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != config.currentPageIndex) {
            actions.onPageChanged(pagerState.currentPage)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            AppWizardHeader(title = config.title, progress = config.progress)

            HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
                pagerContent(page)
            }

            AppWizardActions(
                primaryButtonText = config.primaryButtonText,
                isPrimaryEnabled = config.isPrimaryEnabled,
                onPrimaryClick = actions.onPrimaryClick,
                secondaryButtonText = config.secondaryButtonText,
                onSecondaryClick = actions.onSecondaryClick,
            )

            AppWizardPageIndicator(
                pageCount = config.pageCount,
                currentPage = pagerState.currentPage,
            )
        }
    }
}

@Composable
private fun AppWizardHeader(
    title: String,
    progress: Float,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(modifier = Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun AppWizardActions(
    primaryButtonText: String,
    isPrimaryEnabled: Boolean,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String?,
    onSecondaryClick: (() -> Unit)?,
) {
    Spacer(modifier = Modifier.height(20.dp))

    if ((secondaryButtonText != null) && (onSecondaryClick != null)) {
        AppSecondaryButton(
            text = secondaryButtonText,
            onClick = onSecondaryClick,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
    }

    AppPrimaryButton(
        text = primaryButtonText,
        onClick = onPrimaryClick,
        enabled = isPrimaryEnabled,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun AppWizardPageIndicator(
    pageCount: Int,
    currentPage: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (index in 0 until pageCount) {
            val isActive = index == currentPage
            Surface(
                modifier = Modifier.size(width = if (isActive) 18.dp else 8.dp, height = 8.dp),
                shape = RoundedCornerShape(999.dp),
                color =
                    if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    },
            ) {}
            if (index < (pageCount - 1)) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview
@Composable
fun AppWizardBottomSheetPreview() {
    UnitWiseTheme {
        AppWizardBottomSheet(
            config =
                AppWizardBottomSheetConfig(
                    title = "Step 1 of 3",
                    progress = 0.33f,
                    pageCount = 3,
                    currentPageIndex = 0,
                    primaryButtonText = "Next",
                    secondaryButtonText = "Scan Again",
                ),
            actions =
                AppWizardBottomSheetActions(
                    onPageChanged = {},
                    onPrimaryClick = {},
                ) {},
            pagerContent = {
                Text(text = "Preview Content for page $it")
            },
        )
    }
}
