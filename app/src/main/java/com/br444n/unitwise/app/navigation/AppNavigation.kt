package com.br444n.unitwise.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.br444n.unitwise.app.feature.home.HomeScreen

object Screen {
    const val HOME = "home"
    const val HISTORY = "history" // Placeholder for future
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            HomeScreen()
        }
        composable(Screen.HISTORY) {
            // TODO: Add HistoryScreen when ready
        }
    }
}
