package com.br444n.unitwise.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.Alignment
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
import com.br444n.unitwise.app.ui.components.rememberBottomNavVisibility
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseLoading
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

private val BottomNavOverlayPadding = 96.dp

private data class HomeContentCallbacks(
    val onNavigateToComparison: (Int) -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit,
    val handleScanClick: (String) -> Unit,
    val onUpdateProductA: (ProductInputState) -> Unit,
    val onUpdateProductB: (ProductInputState) -> Unit,
    val onShowIncompatibleUnitsMessage: () -> Unit,
    val onCalculate: ((Int) -> Unit) -> Unit
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
    val hints: ProductInputHints = ProductInputHints()
)

private fun HomeUiState.otherSelectedUnitFor(driver: UnitSelectionDriver, otherUnit: String): String? {
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
            onCalculate = viewModel::calculate
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
    val isBottomNavVisible = rememberBottomNavVisibility { scrollState.value }
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                UnitWiseTopAppBar(
                    onSettingsClick = callbacks.onNavigateToSettings
                )
            },
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
                        hints = ProductInputHints()
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
                        )
                    ),
                    onShowIncompatibleUnitsMessage = callbacks.onShowIncompatibleUnitsMessage,
                    onScanClick = callbacks.handleScanClick
                )

                CalculateButton(
                    onClick = { callbacks.onCalculate(callbacks.onNavigateToComparison) },
                    enabled = uiState.isCalculateEnabled && !uiState.isLoading,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        } // End Scaffold

        HomeBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            visible = isBottomNavVisible,
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
    } // End Box
} // End HomeScreen

@Composable
private fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onNavigateToHistory: () -> Unit
) {
    UnitWiseBottomNavigation(
        modifier = modifier,
        visible = visible,
        onNavigate = { index ->
            when (index) {
                1 -> onNavigateToHistory()
                else -> { /* already on Home */ }
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
            otherSelectedUnit = config.otherSelectedUnit
        )
    )
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
                onCalculate = {}
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
