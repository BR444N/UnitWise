package com.br444n.unitwise.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.br444n.unitwise.app.feature.home.components.UnitWiseTopAppBar
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseLoading
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                UnitWiseTopAppBar(
                    onSettingsClick = onNavigateToSettings
                )
            },
            bottomBar = {
                UnitWiseBottomNavigation(
                    onNavigate = { index ->
                        when (index) {
                            1 -> onNavigateToHistory()
                            else -> { /* already on Home */ }
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeaderText(modifier = Modifier.padding(top = 16.dp))

                ProductInputCard(
                    title = stringResource(id = R.string.product_a_title),
                    state = uiState.productA,
                    actions = ProductInputActions(
                        onProductNameChange = {
                            viewModel.updateProductA(
                                uiState.productA.copy(
                                    productName = it
                                )
                            )
                        },
                        onContentAmountChange = {
                            viewModel.updateProductA(
                                uiState.productA.copy(
                                    contentAmount = it
                                )
                            )
                        },
                        onUnitChange = { viewModel.updateProductA(uiState.productA.copy(selectedUnit = it)) },
                        onPriceChange = { viewModel.updateProductA(uiState.productA.copy(price = it)) },
                        onQuantityChange = { viewModel.updateProductA(uiState.productA.copy(quantity = it)) },
                        onScanClick = { handleScanClick("A") }
                    ),
                    focusConfig = ProductInputFocusConfig(
                        productName = productANameFocus,
                        contentAmount = productAContentFocus,
                        unit = productAUnitFocus,
                        price = productAPriceFocus,
                        quantity = productAQuantityFocus,
                        nextProductName = productBNameFocus
                    )
                )

                ProductInputCard(
                    title = stringResource(id = R.string.product_b_title),
                    state = uiState.productB,
                    actions = ProductInputActions(
                        onProductNameChange = {
                            viewModel.updateProductB(
                                uiState.productB.copy(
                                    productName = it
                                )
                            )
                        },
                        onContentAmountChange = {
                            viewModel.updateProductB(
                                uiState.productB.copy(
                                    contentAmount = it
                                )
                            )
                        },
                        onUnitChange = { viewModel.updateProductB(uiState.productB.copy(selectedUnit = it)) },
                        onPriceChange = { viewModel.updateProductB(uiState.productB.copy(price = it)) },
                        onQuantityChange = { viewModel.updateProductB(uiState.productB.copy(quantity = it)) },
                        onScanClick = { handleScanClick("B") }
                    ),
                    focusConfig = ProductInputFocusConfig(
                        productName = productBNameFocus,
                        contentAmount = productBContentFocus,
                        unit = productBUnitFocus,
                        price = productBPriceFocus,
                        quantity = productBQuantityFocus
                    )
                )

                CalculateButton(
                    onClick = { viewModel.calculate(onNavigateToComparison) },
                    enabled = uiState.isCalculateEnabled && !uiState.isLoading,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        } // End Scaffold

        if (uiState.isLoading) {
            UnitWiseLoading()
        }
    } // End Box
} // End HomeScreen

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    UnitWiseTheme {
        HomeScreen(
            onNavigateToComparison = {},
            onNavigateToScann = {}
        )
    }
}
