package com.br444n.unitwise.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.home.components.CalculateButton
import com.br444n.unitwise.app.feature.home.components.HomeHeaderText
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.br444n.unitwise.app.feature.home.components.UnitWiseTopAppBar
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var productAState by remember { mutableStateOf(ProductInputState()) }
    var productBState by remember { mutableStateOf(ProductInputState()) }

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
        modifier = modifier.fillMaxSize()
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
                state = productAState,
                actions = ProductInputActions(
                    onProductNameChange = { productAState = productAState.copy(productName = it) },
                    onContentAmountChange = { productAState = productAState.copy(contentAmount = it) },
                    onUnitChange = { productAState = productAState.copy(selectedUnit = it) },
                    onPriceChange = { productAState = productAState.copy(price = it) },
                    onQuantityChange = { productAState = productAState.copy(quantity = it) },
                    onScanClick = { /* Open Scanner for A */ }
                )
            )

            ProductInputCard(
                title = stringResource(id = R.string.product_b_title),
                state = productBState,
                actions = ProductInputActions(
                    onProductNameChange = { productBState = productBState.copy(productName = it) },
                    onContentAmountChange = { productBState = productBState.copy(contentAmount = it) },
                    onUnitChange = { productBState = productBState.copy(selectedUnit = it) },
                    onPriceChange = { productBState = productBState.copy(price = it) },
                    onQuantityChange = { productBState = productBState.copy(quantity = it) },
                    onScanClick = { /* Open Scanner for B */ }
                )
            )

            CalculateButton(
                onClick = { /* Implement calculation logic */ },
                enabled = true,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    UnitWiseTheme {
        HomeScreen()
    }
}
