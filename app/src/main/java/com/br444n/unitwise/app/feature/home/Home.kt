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
import com.br444n.unitwise.app.permission.rememberCameraPermissionHandler
import com.br444n.unitwise.app.feature.home.components.CalculateButton
import com.br444n.unitwise.app.feature.home.components.HomeHeaderText
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.feature.home.components.ProductInputFocusConfig
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
    val onCalculate: ((Int) -> Unit) -> Unit
)

private data class HomeFocusConfigs(
    val productA: ProductInputFocusConfig,
    val productB: ProductInputFocusConfig
)

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
    @Suppress("UNUSED_VARIABLE")
    val handleScanClick: (String) -> Unit = rememberCameraPermissionHandler(
        onPermissionGranted = { target -> onNavigateToScann(target) }
    )

    HomeContent(
        modifier = modifier,
        uiState = uiState,
        callbacks = HomeContentCallbacks(
            onNavigateToComparison = onNavigateToComparison,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToSettings = onNavigateToSettings,
            handleScanClick = handleScanClick,
            onUpdateProductA = viewModel::updateProductA,
            onUpdateProductB = viewModel::updateProductB,
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

                ProductInputCard(
                    title = stringResource(id = R.string.product_a_title),
                    state = uiState.productA,
                    actions = ProductInputActions(
                        onProductNameChange = {
                            callbacks.onUpdateProductA(
                                uiState.productA.copy(
                                    productName = it
                                )
                            )
                        },
                        onContentAmountChange = {
                            callbacks.onUpdateProductA(
                                uiState.productA.copy(
                                    contentAmount = it
                                )
                            )
                        },
                        onUnitChange = { callbacks.onUpdateProductA(uiState.productA.copy(selectedUnit = it)) },
                        onPriceChange = { callbacks.onUpdateProductA(uiState.productA.copy(price = it)) },
                        onQuantityChange = { callbacks.onUpdateProductA(uiState.productA.copy(quantity = it)) },
                        onScanClick = { callbacks.handleScanClick("A") }
                    ),
                    focusConfig = focusConfigs.productA
                )

                ProductInputCard(
                    title = stringResource(id = R.string.product_b_title),
                    state = uiState.productB,
                    actions = ProductInputActions(
                        onProductNameChange = {
                            callbacks.onUpdateProductB(
                                uiState.productB.copy(
                                    productName = it
                                )
                            )
                        },
                        onContentAmountChange = {
                            callbacks.onUpdateProductB(
                                uiState.productB.copy(
                                    contentAmount = it
                                )
                            )
                        },
                        onUnitChange = { callbacks.onUpdateProductB(uiState.productB.copy(selectedUnit = it)) },
                        onPriceChange = { callbacks.onUpdateProductB(uiState.productB.copy(price = it)) },
                        onQuantityChange = { callbacks.onUpdateProductB(uiState.productB.copy(quantity = it)) },
                        onScanClick = { callbacks.handleScanClick("B") }
                    ),
                    focusConfig = focusConfigs.productB
                )

                CalculateButton(
                    onClick = { callbacks.onCalculate(callbacks.onNavigateToComparison) },
                    enabled = uiState.isCalculateEnabled && !uiState.isLoading,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        } // End Scaffold

        UnitWiseBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            visible = isBottomNavVisible,
            onNavigate = { index ->
                when (index) {
                    1 -> callbacks.onNavigateToHistory()
                    else -> { /* already on Home */ }
                }
            }
        )

        if (uiState.isLoading) {
            UnitWiseLoading()
        }
    } // End Box
} // End HomeScreen

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
