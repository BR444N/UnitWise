package com.br444n.unitwise.app.feature.comparison

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.feature.comparison.components.BestValueWrapper
import com.br444n.unitwise.app.feature.comparison.components.SmartChoiceBadge
import com.br444n.unitwise.app.feature.comparison.components.WhyBetterSection
import com.br444n.unitwise.app.feature.home.HomeViewModel
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseDefaultTopAppBar
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ComparisonScreen(
    onBackClick: () -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    viewModel: ComparisonViewModel = viewModel()
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(homeState.productA, homeState.productB) {
        viewModel.calculate(homeState.productA, homeState.productB)
    }

    val winningProductName = uiState.winningProduct.productName.ifBlank { "Product" }
    
    Scaffold(
        topBar = {
            UnitWiseDefaultTopAppBar(
                // Intentionally leaving title empty or providing a default if needed
                title = stringResource(id = R.string.comparison_result),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            UnitWiseBottomNavigation(
                onNavigate = { /* Handle navigation if needed */ }
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
            Spacer(modifier = Modifier.height(8.dp))
            
            SmartChoiceBadge(productName = winningProductName)

            BestValueWrapper {
                ProductInputCard(
                    title = if (uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
                    state = uiState.winningProduct,
                    // Pass empty actions for read-only view
                    actions = ProductInputActions()
                )
            }

            WhyBetterSection(
                savingsPerStandardUnit = uiState.savingsPerStandardUnit,
                standardUnitDesc = uiState.standardUnitDesc,
                estimatedMonthlySavings = uiState.monthlySavings
            )

            ProductInputCard(
                title = if (!uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
                state = uiState.losingProduct,
                // Pass empty actions for read-only view
                actions = ProductInputActions()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ComparisonScreenPreview() {
    UnitWiseTheme {
        // For preview purposes, we can instantiate an empty HomeViewModel
        // Note: In real scenarios, prefer breaking down the UI to take State instead of ViewModel for previews.
        ComparisonScreen(
            onBackClick = {},
            homeViewModel = HomeViewModel()
        )
    }
}
