package com.br444n.unitwise.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.navArgument
import com.br444n.unitwise.app.feature.comparison.ComparisonScreen
import com.br444n.unitwise.app.feature.history.HistoryScreen
import com.br444n.unitwise.app.feature.home.HomeScreen
import com.br444n.unitwise.app.feature.home.HomeViewModel
import com.br444n.unitwise.app.feature.scann.ScannScreen

object Screen {
    const val HOME = "home"
    const val COMPARISON = "comparison/{id}"
    fun createComparisonRoute(id: Int) = "comparison/$id"
    const val HISTORY = "history" // Placeholder for future
    const val SCANN = "scann/{targetProduct}"
    fun createScannRoute(targetProduct: String) = "scann/$targetProduct"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Scoped to the NavHost so both screens share the same data context 
    // although they use decoupled instances after Room db insertion
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) { backStackEntry ->
            val resultA: String? = backStackEntry.savedStateHandle["scann_result_A"]
            val resultB: String? = backStackEntry.savedStateHandle["scann_result_B"]

            LaunchedEffect(resultA) {
                if (resultA != null) {
                    val current = homeViewModel.uiState.value.productA
                    homeViewModel.updateProductA(current.copy(productName = resultA))
                    backStackEntry.savedStateHandle.remove<String>("scann_result_A")
                }
            }

            LaunchedEffect(resultB) {
                if (resultB != null) {
                    val current = homeViewModel.uiState.value.productB
                    homeViewModel.updateProductB(current.copy(productName = resultB))
                    backStackEntry.savedStateHandle.remove<String>("scann_result_B")
                }
            }

            HomeScreen(
                onNavigateToComparison = { id -> 
                    navController.navigate(Screen.createComparisonRoute(id)) 
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.HISTORY)
                },
                onNavigateToScann = { targetProduct ->
                    navController.navigate(Screen.createScannRoute(targetProduct))
                },
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
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.HISTORY) {
            HistoryScreen(
                onNavigate = { index ->
                    when (index) {
                        0 -> navController.navigate(Screen.HOME) {
                            popUpTo(Screen.HOME) { inclusive = true }
                        }
                        1 -> { /* already here */ }
                    }
                },
                onViewDetails = { id ->
                    navController.navigate(Screen.createComparisonRoute(id))
                }
            )
        }
        composable(
            route = Screen.SCANN,
            arguments = listOf(navArgument("targetProduct") { type = NavType.StringType })
        ) { backStackEntry ->
            val targetProduct = backStackEntry.arguments?.getString("targetProduct") ?: return@composable
            ScannScreen(
                onBackClick = { navController.popBackStack() },
                onUseSelectedClick = { selectedText ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("scann_result_$targetProduct", selectedText)
                    navController.popBackStack()
                }
            )
        }
    }
}
