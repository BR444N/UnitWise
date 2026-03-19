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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.home.components.CalculateButton
import com.br444n.unitwise.app.feature.home.components.HomeHeaderText
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.feature.home.components.UnitWiseTopAppBar
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseLoading
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToComparison: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                UnitWiseTopAppBar(
                    onSettingsClick = { /* Navigate to settings */ }
                )
            },
            bottomBar = {
                UnitWiseBottomNavigation(
                    onNavigate = { /* Handle navigation between Home and History */ }
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
                        onScanClick = { /* Open Scanner for A */ }
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
                        onScanClick = { /* Open Scanner for B */ }
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
            onNavigateToComparison = {}
        )
    }
}
