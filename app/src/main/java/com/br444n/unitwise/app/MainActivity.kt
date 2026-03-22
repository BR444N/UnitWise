package com.br444n.unitwise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.unitwise.app.navigation.AppNavigation
import com.br444n.unitwise.app.ui.MainViewModel
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            UnitWiseTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}
