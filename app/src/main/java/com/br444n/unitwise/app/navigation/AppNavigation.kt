package com.br444n.unitwise.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.br444n.unitwise.app.feature.comparison.ComparisonScreen
import com.br444n.unitwise.app.feature.home.HomeScreen
import com.br444n.unitwise.app.feature.home.HomeViewModel

object Screen {
    const val HOME = "home"
    const val COMPARISON = "comparison"
    const val HISTORY = "history" // Placeholder for future
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Scoped to the NavHost so both screens share the same data
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            HomeScreen(
                onNavigateToComparison = { navController.navigate(Screen.COMPARISON) },
                viewModel = homeViewModel
            )
        }
        composable(Screen.COMPARISON) {
            ComparisonScreen(
                onBackClick = { navController.popBackStack() },
                homeViewModel = homeViewModel
            )
        }
        composable(Screen.HISTORY) {
            // TODO: Add HistoryScreen when ready
        }
    }
}
