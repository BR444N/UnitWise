package com.br444n.unitwise.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.R
import com.br444n.unitwise.app.core.ui.components.buttons.AppFloatingActionButton
import com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenu
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextField
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldConfig
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldContent
import com.br444n.unitwise.app.core.ui.components.inputs.AppTextFieldKeyboard
import com.br444n.unitwise.app.core.ui.components.layout.AppCard
import com.br444n.unitwise.app.core.ui.components.messages.AppToastMessage
import com.br444n.unitwise.app.core.ui.components.navigation.AppTopBar
import com.br444n.unitwise.app.domain.model.MeasurementUnit
import com.br444n.unitwise.app.domain.model.MeasurementUnit.SUPPORTED_UNITS
import com.br444n.unitwise.app.navigation.components.UnitWiseBottomNavigation
import com.br444n.unitwise.app.ui.components.UnitWiseLoading
import com.br444n.unitwise.app.ui.theme.BrandPrimary
import com.br444n.unitwise.app.ui.theme.BrandPrimaryUnfocused
import com.br444n.unitwise.app.ui.theme.DarkBackgroundMain
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.joco.compose_showcaseview.ShowcaseAlignment
import com.joco.compose_showcaseview.ShowcasePosition
import com.joco.compose_showcaseview.ShowcaseView
import com.joco.compose_showcaseview.highlight.ShowcaseHighlight

import com.br444n.unitwise.app.domain.model.ProductInputState

private val BottomNavOverlayPadding = 96.dp

private enum class HomeShowcaseStep {
    SCAN_BUTTON,
    PRODUCT_A_CARD,
    PRODUCT_B_CARD
}

data class ProductInputHints(
    val productNameHint: Int = R.string.scan_hint,
    val contentAmountLabel: Int = R.string.content_label,
    val priceLabel: Int = R.string.price_label
)

data class ProductInputFocusConfig(
    val productName: FocusRequester,
    val contentAmount: FocusRequester,
    val unit: FocusRequester,
    val price: FocusRequester,
    val quantity: FocusRequester,
    val nextProductName: FocusRequester? = null
)

private const val PRODUCT_NAME_MAX_LENGTH = 24
private const val CONTENT_AMOUNT_MAX_LENGTH = 7
private const val PRICE_MAX_LENGTH = 7
private const val QUANTITY_MAX_LENGTH = 3

private fun sanitizeProductNameInput(input: String): String {
    return input.take(PRODUCT_NAME_MAX_LENGTH)
}

private fun sanitizeDecimalInput(input: String, maxLength: Int): String {
    val normalized = buildString(input.length) {
        var hasDecimalSeparator = false
        input.forEach { char ->
            when {
                char.isDigit() -> append(char)
                (char == '.' || char == ',') && !hasDecimalSeparator -> {
                    append('.')
                    hasDecimalSeparator = true
                }
            }
        }
    }
    return normalized.take(maxLength)
}

private fun sanitizeQuantityInput(input: String): String {
    return input.filter(Char::isDigit).take(QUANTITY_MAX_LENGTH)
}

private data class HomeContentCallbacks(
    val onNavigateToComparison: (Int) -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToShoppingList: () -> Unit,
    val onNavigateToSettings: () -> Unit,
    val handleScanClick: (String) -> Unit,
    val onUpdateProductA: (ProductInputState) -> Unit,
    val onUpdateProductB: (ProductInputState) -> Unit,
    val onShowIncompatibleUnitsMessage: () -> Unit,
    val onCalculate: ((Int) -> Unit) -> Unit,
    val onCalculateInline: (Int, () -> Unit) -> Unit,
    val onCancelInlineComparison: () -> Unit,
    val onCompleteHomeOnboarding: () -> Unit,
    val onPopBackStack: () -> Unit,
    val onResetNavigation: () -> Unit
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

data class HomeNavigationActions(
    val onNavigateToComparison: (Int) -> Unit = {},
    val onNavigateToHistory: () -> Unit = {},
    val onNavigateToShoppingList: () -> Unit = {},
    val onNavigateToScann: (String) -> Unit = {},
    val onNavigateToSettings: () -> Unit = {},
    val onPopBackStack: () -> Unit = {},
    val onResetNavigation: () -> Unit = {}
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigationActions: HomeNavigationActions,
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
            onNavigateToComparison = navigationActions.onNavigateToComparison,
            onNavigateToHistory = navigationActions.onNavigateToHistory,
            onNavigateToShoppingList = navigationActions.onNavigateToShoppingList,
            onNavigateToSettings = navigationActions.onNavigateToSettings,
            handleScanClick = navigationActions.onNavigateToScann,
            onUpdateProductA = viewModel::updateProductA,
            onUpdateProductB = viewModel::updateProductB,
            onShowIncompatibleUnitsMessage = viewModel::showIncompatibleUnitsMessage,
            onCalculate = viewModel::calculate,
            onCalculateInline = viewModel::calculateInline,
            onCancelInlineComparison = {
                viewModel.cancelInlineComparison()
                navigationActions.onResetNavigation()
            },
            onCompleteHomeOnboarding = viewModel::completeHomeOnboarding,
            onPopBackStack = navigationActions.onPopBackStack,
            onResetNavigation = navigationActions.onResetNavigation
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
                AppTopBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = stringResource(id = R.string.logo_desc),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        HomeSettingsAction(onSettingsClick = callbacks.onNavigateToSettings)
                    }
                )
            },
            floatingActionButton = {
                HomeFloatingActionButton(
                    inlineComparisonItemId = uiState.inlineComparisonItemId,
                    isCalculateEnabled = uiState.isCalculateEnabled,
                    isLoading = uiState.isLoading,
                    callbacks = callbacks
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
                // Header Text inlined
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home_header_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.home_header_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

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

            }
        } // End Scaffold

        HomeBottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onNavigateToHome = callbacks.onCancelInlineComparison,
            onNavigateToHistory = callbacks.onNavigateToHistory,
            onNavigateToShoppingList = callbacks.onNavigateToShoppingList
        )

        AppToastMessage(
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
                showcaseStep = getNextShowcaseStep(showcaseStep, callbacks.onCompleteHomeOnboarding)
            },
            onSkip = callbacks.onCompleteHomeOnboarding
        )
    } // End Box
} // End HomeScreen

@Composable
private fun HomeFloatingActionButton(
    inlineComparisonItemId: Int?,
    isCalculateEnabled: Boolean,
    isLoading: Boolean,
    callbacks: HomeContentCallbacks
) {
    AppFloatingActionButton(
        text = if (inlineComparisonItemId != null) stringResource(id = R.string.save_to_list) else "Calculate",
        icon = if (inlineComparisonItemId != null) Icons.Default.Save else Icons.Default.Calculate,
        onClick = {
            if (inlineComparisonItemId != null) {
                callbacks.onCalculateInline(inlineComparisonItemId) {
                    callbacks.onPopBackStack()
                }
            } else {
                callbacks.onCalculate(callbacks.onNavigateToComparison)
            }
        },
        enabled = isCalculateEnabled && !isLoading,
        modifier = Modifier.padding(bottom = BottomNavOverlayPadding)
    )
}

private fun getNextShowcaseStep(current: HomeShowcaseStep, onComplete: () -> Unit): HomeShowcaseStep {
    return when (current) {
        HomeShowcaseStep.SCAN_BUTTON -> HomeShowcaseStep.PRODUCT_A_CARD
        HomeShowcaseStep.PRODUCT_A_CARD -> HomeShowcaseStep.PRODUCT_B_CARD
        HomeShowcaseStep.PRODUCT_B_CARD -> {
            onComplete()
            HomeShowcaseStep.PRODUCT_B_CARD
        }
    }
}

@Composable
private fun HomeBottomNavigation(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToShoppingList: () -> Unit
) {
    UnitWiseBottomNavigation(
        modifier = modifier,
        visible = true,
        onNavigate = { index ->
            when (index) {
                0 -> onNavigateToHome()
                1 -> onNavigateToShoppingList()
                2 -> onNavigateToHistory()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeProductInputCard(
    config: ProductCardContentConfig,
    onShowIncompatibleUnitsMessage: () -> Unit,
    onScanClick: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val onFocusChange: (Boolean) -> Unit = { focused -> if (focused) isFocused = true }

    AppCard(
        modifier = config.cardModifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header (Title & Dot)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when {
                                config.state.isValid() -> MaterialTheme.colorScheme.primary
                                isFocused -> BrandPrimaryUnfocused
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = config.titleResId),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Name Row
            HomeProductNameField(
                config = config,
                onFocusChange = onFocusChange,
                onScanClick = onScanClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content Amount & Unit Row
            HomeProductContentRow(
                config = config,
                onFocusChange = onFocusChange,
                onShowIncompatibleUnitsMessage = onShowIncompatibleUnitsMessage
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Price & Quantity Row
            HomeProductPriceQuantityRow(
                config = config,
                onFocusChange = onFocusChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeProductNameField(
    config: ProductCardContentConfig,
    onFocusChange: (Boolean) -> Unit,
    onScanClick: (String) -> Unit
) {
    val productNameModifier = Modifier
        .fillMaxWidth()
        .onFocusChanged { onFocusChange(it.isFocused) }
        .focusRequester(config.focusConfig.productName)
        
    AppTextField(
        value = config.state.productName,
        onValueChange = { 
            config.onUpdateProduct(config.state.copy(productName = sanitizeProductNameInput(it))) 
        },
        modifier = productNameModifier,
        keyboard = AppTextFieldKeyboard(
            options = KeyboardOptions(imeAction = ImeAction.Next),
            actions = KeyboardActions(
                onNext = { config.focusConfig.contentAmount.requestFocus() }
            )
        ),
        content = AppTextFieldContent(
            placeholder = { Text(stringResource(id = config.hints.productNameHint)) },
            trailingIcon = {
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
                                text = stringResource(id = R.string.scan_desc),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(
                        onClick = { onScanClick(config.scanTarget) },
                        modifier = config.scanButtonModifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(id = R.string.scan_desc),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    )
}

@Composable
private fun HomeProductContentRow(
    config: ProductCardContentConfig,
    onFocusChange: (Boolean) -> Unit,
    onShowIncompatibleUnitsMessage: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val contentModifier = Modifier
            .weight(1f)
            .onFocusChanged { onFocusChange(it.isFocused) }
            .focusRequester(config.focusConfig.contentAmount)
        
        AppTextField(
            value = config.state.contentAmount,
            onValueChange = { 
                config.onUpdateProduct(config.state.copy(contentAmount = sanitizeDecimalInput(it, CONTENT_AMOUNT_MAX_LENGTH))) 
            },
            modifier = contentModifier,
            keyboard = AppTextFieldKeyboard(
                options = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                actions = KeyboardActions(
                    onNext = {
                        keyboardController?.hide()
                        config.focusConfig.unit.requestFocus()
                    }
                )
            ),
            content = AppTextFieldContent(
                label = { Text(stringResource(id = config.hints.contentAmountLabel)) }
            )
        )
        
        val compatibleUnits = remember(config.otherSelectedUnit) {
            MeasurementUnit.compatibleUnitsFor(config.otherSelectedUnit)
        }
        
        AppDropdownMenu(
            config = com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenuConfig(
                selectedItem = config.state.selectedUnit,
                items = SUPPORTED_UNITS,
                itemLabel = { it },
                isItemEnabled = { it == config.state.selectedUnit || compatibleUnits.contains(it) },
                label = stringResource(id = R.string.unit_label)
            ),
            actions = com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenuActions(
                onItemSelected = { config.onUpdateProduct(config.state.copy(selectedUnit = it)) },
                onDisabledItemClick = { onShowIncompatibleUnitsMessage() }
            ),
            focusConfig = com.br444n.unitwise.app.core.ui.components.inputs.AppDropdownMenuFocusConfig(
                focusRequester = config.focusConfig.unit,
                nextFocusRequester = config.focusConfig.price
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HomeProductPriceQuantityRow(
    config: ProductCardContentConfig,
    onFocusChange: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val priceModifier = Modifier
            .weight(1f)
            .onFocusChanged { onFocusChange(it.isFocused) }
            .focusRequester(config.focusConfig.price)

        AppTextField(
            value = config.state.price,
            onValueChange = { 
                config.onUpdateProduct(config.state.copy(price = sanitizeDecimalInput(it, PRICE_MAX_LENGTH))) 
            },
            modifier = priceModifier,
            keyboard = AppTextFieldKeyboard(
                options = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                actions = KeyboardActions(
                    onNext = { config.focusConfig.quantity.requestFocus() }
                )
            ),
            content = AppTextFieldContent(
                label = { Text(stringResource(id = config.hints.priceLabel)) }
            )
        )
        
        val quantityModifier = Modifier
            .weight(1f)
            .onFocusChanged { onFocusChange(it.isFocused) }
            .focusRequester(config.focusConfig.quantity)
        
        val isQuantityZero = config.state.quantity.toIntOrNull() == 0
        
        AppTextField(
            value = config.state.quantity,
            onValueChange = { 
                config.onUpdateProduct(config.state.copy(quantity = sanitizeQuantityInput(it))) 
            },
            modifier = quantityModifier,
            config = AppTextFieldConfig(
                isError = isQuantityZero
            ),
            keyboard = AppTextFieldKeyboard(
                options = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (config.focusConfig.nextProductName != null) ImeAction.Next else ImeAction.Done
                ),
                actions = KeyboardActions(
                    onNext = { config.focusConfig.nextProductName?.requestFocus() },
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            ),
            content = AppTextFieldContent(
                label = { Text(stringResource(id = R.string.quantity_label)) },
                supportingText = {
                    if (isQuantityZero) {
                        Text(text = stringResource(id = R.string.quantity_min_error))
                    }
                }
            )
        )
    }
}

private data class ShowcaseStepData(
    val titleRes: Int,
    val bodyRes: Int,
    val dialogAlignment: Alignment,
    val isFinishStep: Boolean,
    val topPadding: androidx.compose.ui.unit.Dp,
    val bottomPadding: androidx.compose.ui.unit.Dp
)

private fun getShowcaseStepData(step: HomeShowcaseStep): ShowcaseStepData {
    return when (step) {
        HomeShowcaseStep.SCAN_BUTTON -> ShowcaseStepData(
            titleRes = R.string.home_showcase_scan_title,
            bodyRes = R.string.home_showcase_scan_body,
            dialogAlignment = Alignment.Center,
            isFinishStep = false,
            topPadding = 0.dp,
            bottomPadding = 0.dp
        )
        HomeShowcaseStep.PRODUCT_A_CARD -> ShowcaseStepData(
            titleRes = R.string.home_showcase_product_a_title,
            bodyRes = R.string.home_showcase_product_a_body,
            dialogAlignment = Alignment.BottomCenter,
            isFinishStep = false,
            topPadding = 0.dp,
            bottomPadding = 32.dp
        )
        HomeShowcaseStep.PRODUCT_B_CARD -> ShowcaseStepData(
            titleRes = R.string.home_showcase_product_b_title,
            bodyRes = R.string.home_showcase_product_b_body,
            dialogAlignment = Alignment.TopCenter,
            isFinishStep = true,
            topPadding = 96.dp,
            bottomPadding = 0.dp
        )
    }
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

    val stepData = getShowcaseStepData(step)
    val actionRes = if (stepData.isFinishStep) R.string.home_showcase_finish else R.string.home_showcase_next
    
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackgroundMain
    val nextButtonColor = if (isDarkTheme) BrandPrimary else DarkBackgroundMain
    val nextButtonContentColor = if (isDarkTheme) DarkBackgroundMain else Color.White

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
        contentAlignment = stepData.dialogAlignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = stepData.topPadding,
                    bottom = stepData.bottomPadding
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(id = stepData.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = stepData.bodyRes),
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
                onNavigateToShoppingList = {},
                onNavigateToSettings = {},
                handleScanClick = {},
                onUpdateProductA = {},
                onUpdateProductB = {},
                onShowIncompatibleUnitsMessage = {},
                onCalculate = {},
                onCalculateInline = { _, _ -> },
                onCancelInlineComparison = {},
                onCompleteHomeOnboarding = {},
                onPopBackStack = {},
                onResetNavigation = {}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSettingsAction(onSettingsClick: () -> Unit) {
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
                    text = stringResource(id = R.string.settings_desc),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_desc),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
