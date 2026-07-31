package com.br444n.unitwise.app.feature.scann.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.bottomsheet.AppWizardBottomSheet
import com.br444n.unitwise.app.core.ui.components.bottomsheet.AppWizardBottomSheetActions
import com.br444n.unitwise.app.core.ui.components.bottomsheet.AppWizardBottomSheetConfig
import com.br444n.unitwise.app.feature.scann.ScanStep
import com.br444n.unitwise.app.feature.scann.ScannUiState
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepContentPage
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepNamePage
import com.br444n.unitwise.app.feature.scann.bottomsheet.pager.StepPricePage

data class ScannBottomSheetActions(
    val onStepChanged: (ScanStep) -> Unit,
    val onNameChanged: (String) -> Unit,
    val onContentChanged: (String) -> Unit,
    val onUnitChanged: (String) -> Unit,
    val onPriceChanged: (String) -> Unit,
    val onScanAgainClick: () -> Unit,
    val onConfirmClick: () -> Unit,
)

@Composable
fun ScannBottomSheet(
    state: ScannUiState,
    actions: ScannBottomSheetActions,
    modifier: Modifier = Modifier,
) {
    AppWizardBottomSheet(
        modifier = modifier,
        config =
            AppWizardBottomSheetConfig(
                title = stringResource(id = R.string.scann_step_title, state.currentStep.number, 3),
                progress = state.currentStep.progress,
                pageCount = ScanStep.entries.size,
                currentPageIndex = state.currentStep.ordinal,
                primaryButtonText = primaryActionLabel(state.currentStep),
                isPrimaryEnabled = isPrimaryActionEnabled(state),
                secondaryButtonText =
                    if ((state.currentStep == ScanStep.NAME) ||
                        (state.currentStep == ScanStep.CONTENT)
                    ) {
                        stringResource(id = R.string.scan_again)
                    } else {
                        null
                    },
            ),
        actions =
            AppWizardBottomSheetActions(
                onPageChanged = { pageIndex ->
                    actions.onStepChanged(ScanStep.entries[pageIndex])
                },
                onPrimaryClick = {
                    if (state.currentStep == ScanStep.PRICE) {
                        actions.onConfirmClick()
                    } else {
                        actions.onStepChanged(ScanStep.entries[state.currentStep.ordinal + 1])
                    }
                },
                onSecondaryClick =
                    if ((state.currentStep == ScanStep.NAME) ||
                        (state.currentStep == ScanStep.CONTENT)
                    ) {
                        actions.onScanAgainClick
                    } else {
                        null
                    },
            ),
        pagerContent = { page ->
            when (ScanStep.entries[page]) {
                ScanStep.NAME ->
                    StepNamePage(
                        productName = state.productName,
                        onNameChanged = actions.onNameChanged,
                    )

                ScanStep.CONTENT ->
                    StepContentPage(
                        content = state.content,
                        selectedUnit = state.selectedUnit,
                        compatibleUnits = state.compatibleUnits,
                        onContentChanged = actions.onContentChanged,
                        onUnitChanged = actions.onUnitChanged,
                    )

                ScanStep.PRICE ->
                    StepPricePage(
                        price = state.price,
                        onPriceChanged = actions.onPriceChanged,
                    )
            }
        },
    )
}

private fun isPrimaryActionEnabled(state: ScannUiState): Boolean =
    when (state.currentStep) {
        ScanStep.NAME -> state.isNameValid
        ScanStep.CONTENT -> state.isContentValid
        ScanStep.PRICE -> state.isDataReady
    }

@Composable
private fun primaryActionLabel(step: ScanStep): String =
    if (step == ScanStep.PRICE) {
        stringResource(id = R.string.scann_inject_product)
    } else {
        stringResource(id = R.string.scann_confirm_continue)
    }
