package com.br444n.unitwise.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.home.components.CalculateButton
import com.br444n.unitwise.app.feature.home.components.HomeHeaderText
import com.br444n.unitwise.app.feature.home.components.HomeToastMessage
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.feature.home.components.ProductInputFocusConfig
import com.br444n.unitwise.app.feature.home.components.ProductInputHints
import com.br444n.unitwise.app.feature.home.components.ProductInputOptions
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.br444n.unitwise.app.feature.home.components.UnitWiseTopAppBar
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseLoading
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.DarkBackgroundMain
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.joco.compose_showcaseview.ShowcaseAlignment
import com.joco.compose_showcaseview.ShowcasePosition
import com.joco.compose_showcaseview.ShowcaseView
import com.joco.compose_showcaseview.highlight.ShowcaseHighlight
import androidx.compose.foundation.layout.size

private val BottomNavOverlayPadding = 96.dp

private enum class HomeShowcaseStep {
    SCAN_BUTTON,
    PRODUCT_A_CARD,
    PRODUCT_B_CARD
}

private data class HomeContentCallbacks(
    val onNavigateToComparison: (Int) -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit,
    val handleScanClick: (String) -> Unit,
    val onUpdateProductA: (ProductInputState) -> Unit,
    val onUpdateProductB: (ProductInputState) -> Unit,
    val onShowIncompatibleUnitsMessage: () -> Unit,
    val onCalculate: ((Int) -> Unit) -> Unit,
    val onCompleteHomeOnboarding: () -> Unit
)

private data class HomeFocusConfigs(
    val productA: ProductInputFocusConfig,
    val productB: ProductInputFocusConfig
)

private data class ProductCardContentConfig(
    val titleResId: Int,
    val state: ProductInputState,
    val otherSelectedUnit: String?,
    val onUpdateProduct: (ProductInputState) -> Unit,
    val scanTarget: String,
    val focusConfig: ProductInputFocusConfig,
    val hints: ProductInputHints = ProductInputHints(),
    val cardModifier: Modifier = Modifier,
    val scanButtonModifier: Modifier = Modifier
)

private fun HomeUiState.otherSelectedUnitFor(
    driver: UnitSelectionDriver,
    otherUnit: String
): String? {
    return if (unitSelectionDriver == driver) otherUnit else null
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToComparison: (Int) -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToScann: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val productANameFocus = remember { FocusRequester() }
    val productAContentFocus = remember { FocusRequester() }
    val productAUnitFocus = remember { FocusRequester() }
    val productAPriceFocus = remember { FocusRequester() }
    val productAQuantityFocus = remember { FocusRequester() }
    val productBNameFocus = remember { FocusRequester() }
    val productBContentFocus = remember { FocusRequester() }
    val productBUnitFocus = remember { FocusRequester() }
    val productBPriceFocus = remember { FocusRequester() }
    val productBQuantityFocus = remember { FocusRequester() }
    HomeContent(
        modifier = modifier,
        uiState = uiState,
        callbacks = HomeContentCallbacks(
            onNavigateToComparison = onNavigateToComparison,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSettings = onNavigateToSettings,
            handleScanClick = onNavigateToScann,
            onUpdateProductA = viewModel::updateProductA,
            onUpdateProductB = viewModel::updateProductB,
            onShowIncompatibleUnitsMessage = viewModel::showIncompatibleUnitsMessage,
            onCalculate = viewModel::calculate,
            onCompleteHomeOnboarding = viewModel::completeHomeOnboarding
        ),
        focusConfigs = HomeFocusConfigs(
            productA = ProductInputFocusConfig(
                productName = productANameFocus,
                contentAmount = productAContentFocus,
                unit = productAUnitFocus,
                price = productAPriceFocus,
                quantity = productAQuantityFocus,
                nextProductName = productBNameFocus
            ),
            productB = ProductInputFocusConfig(
                productName = productBNameFocus,
                contentAmount = productBContentFocus,
                unit = productBUnitFocus,
                price = productBPriceFocus,
                quantity = productBQuantityFocus
            )
        )
    )
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    callbacks: HomeContentCallbacks,
    focusConfigs: HomeFocusConfigs
) {
    val scrollState = rememberScrollState()
    var scanButtonCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var productACardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var productBCardCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var showcaseStep by rememberSaveable { mutableStateOf(HomeShowcaseStep.SCAN_BUTTON) }

    LaunchedEffect(uiState.shouldShowOnboarding) {
        if (uiState.shouldShowOnboarding) {
            showcaseStep = HomeShowcaseStep.SCAN_BUTTON
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                UnitWiseTopAppBar(
                    onSettingsClick = callbacks.onNavigateToSettings
                )
            },
            floatingActionButton = {
                CalculateButton(
                    onClick = { callbacks.onCalculate(callbacks.onNavigateToComparison) },
                    enabled = uiState.isCalculateEnabled && !uiState.isLoading,
                    modifier = Modifier.padding(bottom = BottomNavOverlayPadding)
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = BottomNavOverlayPadding)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeaderText(modifier = Modifier.padding(top = 16.dp))

                HomeProductInputCard(
                    config = ProductCardContentConfig(
                        titleResId = R.string.product_a_title,
                        state = uiState.productA,
                        otherSelectedUnit = uiState.otherSelectedUnitFor(
                            driver = UnitSelectionDriver.PRODUCT_B,
                            otherUnit = uiState.productB.selectedUnit
                        ),
                        onUpdateProduct = callbacks.onUpdateProductA,
                        scanTarget = "A",
                        focusConfig = focusConfigs.productA,
                        hints = ProductInputHints(),
                        cardModifier = Modifier.onGloballyPositioned {
                            productACardCoordinates = it
                        },
                        scanButtonModifier = Modifier.onGloballyPositioned {
                            scanButtonCoordinates = it
                        }
                    ),
                    onShowIncompatibleUnitsMessage = callbacks.onShowIncompatibleUnitsMessage,
                    onScanClick = callbacks.handleScanClick
                )

                HomeProductInputCard(
                    config = ProductCardContentConfig(
                        titleResId = R.string.product_b_title,
                        state = uiState.productB,
                        otherSelectedUnit = uiState.otherSelectedUnitFor(
                            driver = UnitSelectionDriver.PRODUCT_A,
                            otherUnit = uiState.productA.selectedUnit
                        ),
                        onUpdateProduct = callbacks.onUpdateProductB,
                        scanTarget = "B",
                        focusConfig = focusConfigs.productB,
                        hints = ProductInputHints(
                            productNameHint = R.string.scan_hint_b,
                            contentAmountLabel = R.string.content_label_b,
                            priceLabel = R.string.price_label_b
                        ),
                        cardModifier = Modifier.onGloballyPositioned {
                            productBCardCoordinates = it
                        }
                    ),
                    onShowIncompatibleUnitsMessage = callbacks.onShowIncompatibleUnitsMessage,
                    onScanClick = callbacks.handleScanClick
                )

                /* CalculateButton moved to floatingActionButton slot */
            }
        } // End Scaffold

        HomeBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onNavigateToHistory = callbacks.onNavigateToHistory
        )

        HomeToastMessage(
            eventKey = uiState.incompatibleUnitsToastEvent,
            messageResId = R.string.units_cannot_be_compared,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BottomNavOverlayPadding + 16.dp)
        )

        if (uiState.isLoading) {
            UnitWiseLoading()
        }

        HomeShowcaseOverlay(
            shouldShowOnboarding = uiState.shouldShowOnboarding,
            step = showcaseStep,
            scanButtonCoordinates = scanButtonCoordinates,
            productACardCoordinates = productACardCoordinates,
            productBCardCoordinates = productBCardCoordinates,
            onNext = {
                showcaseStep = when (showcaseStep) {
                    HomeShowcaseStep.SCAN_BUTTON -> HomeShowcaseStep.PRODUCT_A_CARD
                    HomeShowcaseStep.PRODUCT_A_CARD -> HomeShowcaseStep.PRODUCT_B_CARD
                    HomeShowcaseStep.PRODUCT_B_CARD -> {
                        callbacks.onCompleteHomeOnboarding()
                        HomeShowcaseStep.PRODUCT_B_CARD
                    }
                }
            },
            onSkip = callbacks.onCompleteHomeOnboarding
        )
    } // End Box
} // End HomeScreen

@Composable
private fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    onNavigateToHistory: () -> Unit
) {
    UnitWiseBottomNavigation(
        modifier = modifier,
        visible = true,
        onNavigate = { index ->
            when (index) {
                1 -> onNavigateToHistory()
                else -> { /* already on Home */
                }
            }
        }
    )
}

@Composable
private fun HomeProductInputCard(
    config: ProductCardContentConfig,
    onShowIncompatibleUnitsMessage: () -> Unit,
    onScanClick: (String) -> Unit
) {
    ProductInputCard(
        title = stringResource(id = config.titleResId),
        state = config.state,
        actions = ProductInputActions(
            onProductNameChange = {
                config.onUpdateProduct(
                    config.state.copy(productName = it)
                )
            },
            onContentAmountChange = {
                config.onUpdateProduct(
                    config.state.copy(contentAmount = it)
                )
            },
            onUnitChange = { config.onUpdateProduct(config.state.copy(selectedUnit = it)) },
            onIncompatibleUnitSelected = onShowIncompatibleUnitsMessage,
            onPriceChange = { config.onUpdateProduct(config.state.copy(price = it)) },
            onQuantityChange = { config.onUpdateProduct(config.state.copy(quantity = it)) },
            onScanClick = { onScanClick(config.scanTarget) }
        ),
        options = ProductInputOptions(
            focusConfig = config.focusConfig,
            hints = config.hints,
            otherSelectedUnit = config.otherSelectedUnit,
            cardModifier = config.cardModifier,
            scanButtonModifier = config.scanButtonModifier
        )
    )
}

@Composable
private fun HomeShowcaseOverlay(
    shouldShowOnboarding: Boolean,
    step: HomeShowcaseStep,
    scanButtonCoordinates: LayoutCoordinates?,
    productACardCoordinates: LayoutCoordinates?,
    productBCardCoordinates: LayoutCoordinates?,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    if (!shouldShowOnboarding) return

    val targetCoordinates = when (step) {
        HomeShowcaseStep.SCAN_BUTTON -> scanButtonCoordinates
        HomeShowcaseStep.PRODUCT_A_CARD -> productACardCoordinates
        HomeShowcaseStep.PRODUCT_B_CARD -> productBCardCoordinates
    } ?: return

    if (!targetCoordinates.isAttached) return

    val titleRes = when (step) {
        HomeShowcaseStep.SCAN_BUTTON -> R.string.home_showcase_scan_title
        HomeShowcaseStep.PRODUCT_A_CARD -> R.string.home_showcase_product_a_title
        HomeShowcaseStep.PRODUCT_B_CARD -> R.string.home_showcase_product_b_title
    }
    val bodyRes = when (step) {
        HomeShowcaseStep.SCAN_BUTTON -> R.string.home_showcase_scan_body
        HomeShowcaseStep.PRODUCT_A_CARD -> R.string.home_showcase_product_a_body
        HomeShowcaseStep.PRODUCT_B_CARD -> R.string.home_showcase_product_b_body
    }
    val actionRes = if (step == HomeShowcaseStep.PRODUCT_B_CARD) {
        R.string.home_showcase_finish
    } else {
        R.string.home_showcase_next
    }
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackgroundMain
    val nextButtonColor = if (isDarkTheme) BrandPrimary else DarkBackgroundMain
    val nextButtonContentColor = if (isDarkTheme) DarkBackgroundMain else Color.White
    val dialogAlignment = when (step) {
        HomeShowcaseStep.SCAN_BUTTON -> Alignment.Center
        HomeShowcaseStep.PRODUCT_A_CARD -> Alignment.BottomCenter
        HomeShowcaseStep.PRODUCT_B_CARD -> Alignment.TopCenter
    }

    ShowcaseView(
        visible = true,
        targetCoordinates = targetCoordinates,
        position = ShowcasePosition.Default,
        alignment = ShowcaseAlignment.CenterHorizontal,
        highlight = ShowcaseHighlight.Rectangular(cornerRadius = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(1.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = dialogAlignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = if (step == HomeShowcaseStep.PRODUCT_B_CARD) 96.dp else 0.dp,
                    bottom = if (step == HomeShowcaseStep.PRODUCT_A_CARD) 32.dp else 0.dp
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = bodyRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onSkip) {
                        Text(text = stringResource(id = R.string.home_showcase_skip))
                    }
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = nextButtonColor,
                            contentColor = nextButtonContentColor
                        )
                    ) { Text(text = stringResource(id = actionRes)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    UnitWiseTheme {
        HomeContent(
            uiState = HomeUiState(
                productA = ProductInputState(
                    productName = "Greek Yogurt",
                    contentAmount = "500",
                    selectedUnit = "g",
                    price = "24.50",
                    quantity = "2"
                ),
                productB = ProductInputState(
                    productName = "Natural Yogurt",
                    contentAmount = "1.25",
                    selectedUnit = "kg",
                    price = "56.90",
                    quantity = "1"
                )
            ),
            callbacks = HomeContentCallbacks(
                onNavigateToComparison = {},
                onNavigateToHistory = {},
                onNavigateToSettings = {},
                handleScanClick = {},
                onUpdateProductA = {},
                onUpdateProductB = {},
                onShowIncompatibleUnitsMessage = {},
                onCalculate = {},
                onCompleteHomeOnboarding = {}
            ),
            focusConfigs = HomeFocusConfigs(
                productA = ProductInputFocusConfig(
                    productName = remember { FocusRequester() },
                    contentAmount = remember { FocusRequester() },
                    unit = remember { FocusRequester() },
                    price = remember { FocusRequester() },
                    quantity = remember { FocusRequester() },
                    nextProductName = remember { FocusRequester() }
                ),
                productB = ProductInputFocusConfig(
                    productName = remember { FocusRequester() },
                    contentAmount = remember { FocusRequester() },
                    unit = remember { FocusRequester() },
                    price = remember { FocusRequester() },
                    quantity = remember { FocusRequester() }
                )
            )
        )
    }
}
