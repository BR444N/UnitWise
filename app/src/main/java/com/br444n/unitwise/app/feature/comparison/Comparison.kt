package com.br444n.unitwise.app.feature.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldConfig
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.app.core.ui.components.layout.AppCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.br444n.unitwise.app.domain.model.ProductInputState
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import com.br444n.unitwise.app.core.ui.components.wrappers.AppHighlightedWrapper
import com.br444n.unitwise.app.feature.share.SharedComparisonData
import com.br444n.unitwise.app.core.ui.components.cards.AppBadgeCard
import com.br444n.unitwise.app.core.ui.components.cards.AppBadgeCardDefaults
import com.br444n.unitwise.app.core.ui.components.lists.AppIconTextRow
import com.br444n.unitwise.app.core.ui.components.lists.AppKeyValueRow
import com.br444n.unitwise.app.ui.theme.Badge
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

@Composable
fun ComparisonScreen(
    modifier: Modifier = Modifier,
    comparisonId: Int? = null,
    sharedComparisonData: SharedComparisonData? = null,
    sharedComparisonLink: SharedComparisonRoute? = null,
    onBackClick: () -> Unit,
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
            AppTopBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.comparison_result),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    ComparisonBackButton(
                        contentDesc = stringResource(id = R.string.navigate_up),
                        onClick = onBackClick
                    )
                }
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
    AppBadgeCard(
        title = stringResource(id = R.string.tie_title),
        subtitle = stringResource(id = R.string.tie_subtitle),
        icon = Icons.Default.Info,
        colors = AppBadgeCardDefaults.colors(containerColor = Badge)
    )

    ComparisonProductCard(
        title = stringResource(R.string.product_a_title),
        state = uiState.productA
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.why_tie_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppKeyValueRow(
            label = stringResource(id = R.string.product_a_title),
            value = stringResource(id = R.string.tie_value_unit_price, uiState.unitPriceA, uiState.standardUnitDesc)
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppKeyValueRow(
            label = stringResource(id = R.string.product_b_title),
            value = stringResource(id = R.string.tie_value_unit_price, uiState.unitPriceB, uiState.standardUnitDesc)
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.tie_explanation),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    ComparisonProductCard(
        title = stringResource(R.string.product_b_title),
        state = uiState.productB
    )
}

@Composable
private fun WinnerResultContent(uiState: ComparisonUiState) {
    val winningProductName = uiState.winningProduct.productName.ifBlank { "Product" }
    
    AppBadgeCard(
        title = stringResource(id = R.string.smart_choice_title),
        subtitle = stringResource(id = R.string.smart_choice_subtitle, winningProductName),
        icon = Icons.Default.ShoppingCart,
        colors = AppBadgeCardDefaults.colors(containerColor = Badge)
    )

    AppHighlightedWrapper(
        badgeText = stringResource(id = R.string.best_value_badge),
        badgeIcon = Icons.Default.Stars
    ) {
        ComparisonProductCard(
            title = if (uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
            state = uiState.winningProduct
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.why_better_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppIconTextRow(
            icon = Icons.Default.CheckCircle,
            text = stringResource(
                id = R.string.why_better_saving_per_unit,
                uiState.savingsPerStandardUnit,
                uiState.standardUnitDesc
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        val winnerPrice = if (uiState.isProductAWinner) uiState.unitPriceA else uiState.unitPriceB
        val loserPrice = if (uiState.isProductAWinner) uiState.unitPriceB else uiState.unitPriceA
        AppIconTextRow(
            icon = Icons.Default.Info,
            text = stringResource(
                id = R.string.why_better_unit_price_comparison,
                winnerPrice,
                loserPrice,
                uiState.standardUnitDesc
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppIconTextRow(
            icon = Icons.Default.CheckCircle,
            text = stringResource(
                id = R.string.why_better_monthly_saving,
                uiState.monthlySavings
            )
        )
    }

    ComparisonProductCard(
        title = if (!uiState.isProductAWinner) stringResource(R.string.product_a_title) else stringResource(R.string.product_b_title),
        state = uiState.losingProduct
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComparisonBackButton(contentDesc: String, onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Below
        ),
        tooltip = {
            PlainTooltip(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = contentDesc,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComparisonProductCard(
    title: String,
    state: ProductInputState
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Name Row
            AppTextField(
                value = state.productName,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                config = AppTextFieldConfig(readOnly = true, enabled = false),
                content = AppTextFieldContent(
                    placeholder = { Text(stringResource(id = R.string.scan_hint)) }
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content Amount & Unit Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = state.contentAmount,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    config = AppTextFieldConfig(readOnly = true, enabled = false),
                    content = AppTextFieldContent(
                        label = { Text(stringResource(id = R.string.content_label)) }
                    )
                )
                
                AppTextField(
                    value = state.selectedUnit,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    config = AppTextFieldConfig(readOnly = true, enabled = false),
                    content = AppTextFieldContent(
                        label = { Text(stringResource(id = R.string.unit_label)) }
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Price & Quantity Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = state.price,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    config = AppTextFieldConfig(readOnly = true, enabled = false),
                    content = AppTextFieldContent(
                        label = { Text(stringResource(id = R.string.price_label)) }
                    )
                )
                
                AppTextField(
                    value = state.quantity,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    config = AppTextFieldConfig(readOnly = true, enabled = false),
                    content = AppTextFieldContent(
                        label = { Text(stringResource(id = R.string.quantity_label)) }
                    )
                )
            }
        }
    }
}
