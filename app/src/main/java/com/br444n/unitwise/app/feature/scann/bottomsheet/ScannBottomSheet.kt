package com.br444n.unitwise.app.feature.scann.bottomsheet

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.scann.ScanStep
import com.br444n.unitwise.app.feature.scann.ScannUiState
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepContentPage
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepNamePage
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepPricePage
import kotlinx.coroutines.launch

data class ScannBottomSheetActions(
    val onStepChanged: (ScanStep) -> Unit,
    val onNameChanged: (String) -> Unit,
    val onContentChanged: (String) -> Unit,
    val onUnitChanged: (String) -> Unit,
    val onPriceChanged: (String) -> Unit,
    val onScanAgainClick: () -> Unit,
    val onConfirmClick: () -> Unit
)

@Composable
fun ScannBottomSheet(
    state: ScannUiState,
    actions: ScannBottomSheetActions,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentStep.ordinal,
        pageCount = { ScanStep.entries.size }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.currentStep) {
        if (pagerState.currentPage != state.currentStep.ordinal) {
            pagerState.animateScrollToPage(state.currentStep.ordinal)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        actions.onStepChanged(ScanStep.entries[pagerState.currentPage])
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            ScannBottomSheetHeader(state = state)
            ScannBottomSheetPager(
                state = state,
                pagerState = pagerState,
                actions = actions
            )
            ScannBottomSheetActionsSection(
                state = state,
                pagerState = pagerState,
                onScanAgainClick = actions.onScanAgainClick,
                onConfirmClick = actions.onConfirmClick,
                onNextClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            )
            ScannBottomSheetPageIndicator(currentPage = pagerState.currentPage)
        }
    }
}

@Composable
private fun ScannBottomSheetHeader(state: ScannUiState) {
    Text(
        text = stringResource(id = R.string.scann_step_title, state.currentStep.number, 3),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { state.currentStep.progress },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ScannBottomSheetPager(
    state: ScannUiState,
    pagerState: PagerState,
    actions: ScannBottomSheetActions
) {
    HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
        when (ScanStep.entries[page]) {
            ScanStep.NAME -> StepNamePage(
                productName = state.productName,
                onNameChanged = actions.onNameChanged
            )

            ScanStep.CONTENT -> StepContentPage(
                content = state.content,
                selectedUnit = state.selectedUnit,
                compatibleUnits = state.compatibleUnits,
                onContentChanged = actions.onContentChanged,
                onUnitChanged = actions.onUnitChanged
            )

            ScanStep.PRICE -> StepPricePage(
                price = state.price,
                onPriceChanged = actions.onPriceChanged
            )
        }
    }
}

@Composable
private fun ScannBottomSheetActionsSection(
    state: ScannUiState,
    pagerState: PagerState,
    onScanAgainClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))

    if (state.currentStep == ScanStep.NAME || state.currentStep == ScanStep.CONTENT) {
        OutlinedButton(
            onClick = onScanAgainClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.scan_again))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    Button(
        onClick = {
            if (state.currentStep == ScanStep.PRICE) onConfirmClick() else onNextClick()
        },
        enabled = isPrimaryActionEnabled(state),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = primaryActionLabel(state.currentStep))
    }

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun ScannBottomSheetPageIndicator(currentPage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScanStep.entries.forEachIndexed { index, _ ->
            val isActive = index == currentPage
            Surface(
                modifier = Modifier.size(width = if (isActive) 18.dp else 8.dp, height = 8.dp),
                shape = RoundedCornerShape(999.dp),
                color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                }
            ) {}
            if (index < ScanStep.entries.lastIndex) {
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

private fun isPrimaryActionEnabled(state: ScannUiState): Boolean {
    return when (state.currentStep) {
        ScanStep.NAME -> state.isNameValid
        ScanStep.CONTENT -> state.isContentValid
        ScanStep.PRICE -> state.isDataReady
    }
}

@Composable
private fun primaryActionLabel(step: ScanStep): String {
    return if (step == ScanStep.PRICE) {
        stringResource(id = R.string.scann_inject_product)
    } else {
        stringResource(id = R.string.scann_confirm_continue)
    }
}
