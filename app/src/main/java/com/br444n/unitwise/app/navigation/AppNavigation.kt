package com.br444n.unitwise.app.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.navArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.navDeepLink
import com.br444n.unitwise.app.feature.comparison.ComparisonScreen
import com.br444n.unitwise.app.feature.comparison.SharedComparisonRoute
import com.br444n.unitwise.app.feature.history.HistoryScreen
import com.br444n.unitwise.app.feature.home.HomeScreen
import com.br444n.unitwise.app.feature.home.HomeViewModel
import com.br444n.unitwise.app.feature.home.UnitSelectionDriver
import com.br444n.unitwise.app.feature.home.components.ProductInputState
import com.br444n.unitwise.app.feature.scann.ScannScreen
import com.br444n.unitwise.app.feature.scann.ScannResult
import com.br444n.unitwise.app.feature.settings.SettingsScreen
import com.br444n.unitwise.app.feature.share.extractSharedComparisonKey
import androidx.compose.runtime.collectAsState

object Screen {
    const val HOME = "home"
    const val COMPARISON = "comparison/{id}"
    fun createComparisonRoute(id: Int) = "comparison/$id"
    const val SHARED_COMPARISON = "comparison/shared/{shareId}"
    const val HISTORY = "history" // Placeholder for future
    const val SCANN = "scann/{targetProduct}"
    fun createScannRoute(targetProduct: String) = "scann/$targetProduct"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    NavHost(navController = navController, startDestination = Screen.HOME) {
        composable(Screen.HOME) { backStackEntry ->
            HomeResultHandler(backStackEntry, homeViewModel)
            HomeScreen(
                onNavigateToComparison = { id -> 
                    navController.navigate(Screen.createComparisonRoute(id)) 
                },
                onNavigateToHistory = { navController.navigate(Screen.HISTORY) },
                onNavigateToScann = { target -> navController.navigate(Screen.createScannRoute(target)) },
                onNavigateToSettings = { navController.navigate(Screen.SETTINGS) },
                viewModel = homeViewModel
            )
        }
        composable(
            route = Screen.COMPARISON,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            ComparisonScreen(
                comparisonId = id,
                onBackClick = { navController.popBackStack() },
                onNavigate = { index -> handleBottomTabNav(index, -1, navController) }
            )
        }
        composable(
            route = Screen.SHARED_COMPARISON,
            arguments = listOf(navArgument("shareId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://unitwise-app.vercel.app/c/{shareId}"
                }
            )
        ) { backStackEntry ->
            val shareId = backStackEntry.arguments?.getString("shareId") ?: return@composable
            @Suppress("DEPRECATION")
            val deepLinkIntent = backStackEntry.arguments
                ?.getParcelable<Intent>(NavController.KEY_DEEP_LINK_INTENT)
            val encryptionKey = extractSharedComparisonKey(deepLinkIntent?.data)

            ComparisonScreen(
                sharedComparisonLink = SharedComparisonRoute(
                    shareId = shareId,
                    encryptionKey = encryptionKey
                ),
                onBackClick = { navController.popBackStack() },
                onNavigate = { index -> handleBottomTabNav(index, -1, navController) }
            )
        }
        composable(Screen.HISTORY) {
            HistoryScreen(
                onNavigate = { index -> handleBottomTabNav(index, 1, navController) },
                onViewDetails = { id -> navController.navigate(Screen.createComparisonRoute(id)) },
                onEditComparison = { id ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("edit_comparison_id", id)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.SCANN,
            arguments = listOf(navArgument("targetProduct") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetProduct = backStackEntry.arguments?.getString("targetProduct") ?: return@composable
            val homeState = homeViewModel.uiState.collectAsState().value
            val inheritedUnit = when (targetProduct) {
                "A" -> homeState.productB.selectedUnit.takeIf {
                    shouldInheritUnitForScan(
                        product = homeState.productB,
                        expectedDriver = UnitSelectionDriver.PRODUCT_B,
                        currentDriver = homeState.unitSelectionDriver
                    )
                }
                "B" -> homeState.productA.selectedUnit.takeIf {
                    shouldInheritUnitForScan(
                        product = homeState.productA,
                        expectedDriver = UnitSelectionDriver.PRODUCT_A,
                        currentDriver = homeState.unitSelectionDriver
                    )
                }
                else -> null
            }
            ScannScreen(
                onBackClick = { navController.popBackStack() },
                inheritedUnit = inheritedUnit,
                onResultClick = { result ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("scann_result_$targetProduct", result)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigate = { index -> handleBottomTabNav(index, 2, navController) }
            )
        }
    }
}

private fun shouldInheritUnitForScan(
    product: ProductInputState,
    expectedDriver: UnitSelectionDriver,
    currentDriver: UnitSelectionDriver?
): Boolean {
    val hasMeaningfulData = product.productName.isNotBlank() ||
        product.contentAmount.isNotBlank() ||
        product.price.isNotBlank()
    return hasMeaningfulData || currentDriver == expectedDriver
}

@Composable
private fun HomeResultHandler(backStackEntry: NavBackStackEntry, homeViewModel: HomeViewModel) {
    val resultA: ScannResult? = backStackEntry.savedStateHandle["scann_result_A"]
    val resultB: ScannResult? = backStackEntry.savedStateHandle["scann_result_B"]
    val editComparisonId: Int? = backStackEntry.savedStateHandle["edit_comparison_id"]

    LaunchedEffect(resultA) {
        if (resultA != null) {
            val current = homeViewModel.uiState.value.productA
            homeViewModel.updateProductA(
                current.copy(
                    productName = resultA.productName,
                    contentAmount = resultA.content,
                    selectedUnit = resultA.selectedUnit,
                    price = resultA.price
                )
            )
            backStackEntry.savedStateHandle.remove<ScannResult>("scann_result_A")
        }
    }

    LaunchedEffect(resultB) {
        if (resultB != null) {
            val current = homeViewModel.uiState.value.productB
            homeViewModel.updateProductB(
                current.copy(
                    productName = resultB.productName,
                    contentAmount = resultB.content,
                    selectedUnit = resultB.selectedUnit,
                    price = resultB.price
                )
            )
            backStackEntry.savedStateHandle.remove<ScannResult>("scann_result_B")
        }
    }

    LaunchedEffect(editComparisonId) {
        if (editComparisonId != null) {
            homeViewModel.loadComparisonForEdit(editComparisonId)
            backStackEntry.savedStateHandle.remove<Int>("edit_comparison_id")
        }
    }
}

private fun handleBottomTabNav(index: Int, current: Int, navController: NavHostController) {
    if (index == current) return
    when (index) {
        0 -> navController.navigate(Screen.HOME) { popUpTo(Screen.HOME) { inclusive = true } }
        1 -> navController.navigate(Screen.HISTORY) { popUpTo(Screen.HOME) }
        2 -> navController.navigate(Screen.SETTINGS) { popUpTo(Screen.HOME) }
    }
}
