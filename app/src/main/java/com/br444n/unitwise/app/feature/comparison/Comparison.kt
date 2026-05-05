package com.br444n.unitwise.app.feature.comparison

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
import com.br444n.unitwise.app.feature.comparison.components.TieBadge
import com.br444n.unitwise.app.feature.comparison.components.TieDetailsSection
import com.br444n.unitwise.app.feature.comparison.components.WhyBetterSection
import com.br444n.unitwise.app.feature.home.components.ProductInputActions
import com.br444n.unitwise.app.feature.home.components.ProductInputCard
import com.br444n.unitwise.app.feature.home.components.ProductInputOptions
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.br444n.unitwise.app.feature.share.SharedComparisonData
import com.br444n.unitwise.app.ui.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseDefaultTopAppBar
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ComparisonScreen(
    modifier: Modifier = Modifier,
    comparisonId: Int? = null,
    sharedComparisonData: SharedComparisonData? = null,
    sharedComparisonLink: SharedComparisonRoute? = null,
    onBackClick: () -> Unit,
    onNavigate: (Int) -> Unit = {},
    viewModel: ComparisonViewModel = viewModel(factory = ComparisonViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(comparisonId, sharedComparisonData, sharedComparisonLink) {
        when {
            sharedComparisonData != null -> viewModel.loadSharedComparison(sharedComparisonData)
            comparisonId != null -> viewModel.loadComparison(comparisonId)
            sharedComparisonLink != null -> viewModel.loadComparisonByShareId(
                shareId = sharedComparisonLink.shareId,
                encryptionKey = sharedComparisonLink.encryptionKey
            )
        }
    }
    
    Scaffold(
        topBar = {
            UnitWiseDefaultTopAppBar(
                title = stringResource(id = R.string.comparison_result),
                navigationContentDescription = stringResource(id = R.string.navigate_up),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            UnitWiseBottomNavigation(
                selectedIndex = -1, // Detail screen, no tab highlighted
                onNavigate = onNavigate
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        ComparisonContent(
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

data class SharedComparisonRoute(
    val shareId: String,
    val encryptionKey: String?
)

@Composable
private fun ComparisonContent(
    uiState: ComparisonUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isTie) {
            TieResultContent(uiState)
        } else {
            WinnerResultContent(uiState)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TieResultContent(uiState: ComparisonUiState) {
    TieBadge()

    ProductInputCard(
        title = stringResource(R.string.product_a_title),
        state = uiState.productA,
        actions = ProductInputActions(),
        options = ProductInputOptions(isReadOnly = true)
    )

    TieDetailsSection(
        unitPriceA = uiState.unitPriceA,
        unitPriceB = uiState.unitPriceB,
        standardUnitDesc = uiState.standardUnitDesc
    )

    ProductInputCard(
        title = stringResource(R.string.product_b_title),
        state = uiState.productB,
        actions = ProductInputActions(),
        options = ProductInputOptions(isReadOnly = true)
    )
}

@Composable
private fun WinnerResultContent(uiState: ComparisonUiState) {
    val winningProductName = uiState.winningProduct.productName.ifBlank { "Product" }
    
    SmartChoiceBadge(productName = winningProductName)

    BestValueWrapper {
        ProductInputCard(
            title = if (uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
            state = uiState.winningProduct,
            actions = ProductInputActions(),
            options = ProductInputOptions(isReadOnly = true)
        )
    }

    WhyBetterSection(
        savingsPerStandardUnit = uiState.savingsPerStandardUnit,
        standardUnitDesc = uiState.standardUnitDesc,
        estimatedMonthlySavings = uiState.monthlySavings,
        winnerUnitPrice = if (uiState.isProductAWinner) uiState.unitPriceA else uiState.unitPriceB,
        loserUnitPrice = if (uiState.isProductAWinner) uiState.unitPriceB else uiState.unitPriceA
    )

    ProductInputCard(
        title = if (!uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
        state = uiState.losingProduct,
        actions = ProductInputActions(),
        options = ProductInputOptions(isReadOnly = true)
    )
}

@Preview(showBackground = true, name = "Winner Result")
@Composable
fun ComparisonScreenWinnerPreview() {
    UnitWiseTheme {
        ComparisonContent(
            uiState = ComparisonUiState(
                productA = ProductInputState(
                    productName = "Water",
                    contentAmount = "1",
                    selectedUnit = "l",
                    price = "25.00",
                    quantity = "1"
                ),
                productB = ProductInputState(
                    productName = "Juice",
                    contentAmount = "600",
                    selectedUnit = "ml",
                    price = "18.00",
                    quantity = "1"
                ),
                isProductAWinner = true,
                isTie = false,
                savingsTotal = "5.00",
                monthlySavings = "20.00",
                savingsPerStandardUnit = "5.00",
                standardUnitDesc = "1 l",
                unitPriceA = "25.00",
                unitPriceB = "30.00"
            )
        )
    }
}

@Preview(showBackground = true, name = "Tie Result")
@Composable
fun ComparisonScreenTiePreview() {
    UnitWiseTheme {
        ComparisonContent(
            uiState = ComparisonUiState(
                productA = ProductInputState(
                    productName = "Product A",
                    contentAmount = "500",
                    selectedUnit = "g",
                    price = "10.00",
                    quantity = "1"
                ),
                productB = ProductInputState(
                    productName = "Product B",
                    contentAmount = "500",
                    selectedUnit = "g",
                    price = "10.00",
                    quantity = "1"
                ),
                isTie = true,
                standardUnitDesc = "100 g",
                unitPriceA = "2.00",
                unitPriceB = "2.00"
            )
        )
    }
}
