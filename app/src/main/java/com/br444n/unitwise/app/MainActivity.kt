package com.br444n.unitwise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import com.br444n.unitwise.app.navigation.AppNavigation
import com.br444n.unitwise.app.ui.MainViewModel
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        val viewModel = ViewModelProvider(this, MainViewModel.Factory)[MainViewModel::class.java]
        
        splashScreen.setKeepOnScreenCondition {
            viewModel.isDarkTheme.value == null
        }

        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            if (isDarkTheme != null) {
                UnitWiseTheme(darkTheme = isDarkTheme!!) {
                    AppNavigation()
                }
            }
        }
    }
}
