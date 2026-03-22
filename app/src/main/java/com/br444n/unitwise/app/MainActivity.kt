package com.br444n.unitwise.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import com.br444n.unitwise.app.navigation.AppNavigation
import com.br444n.unitwise.app.ui.MainViewModel
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.ui.util.LocaleHelper

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("user_prefs", MODE_PRIVATE)
        val lang = prefs.getString("selected_language", "en") ?: "en"
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        val viewModel = ViewModelProvider(this, MainViewModel.Factory)[MainViewModel::class.java]
        
        splashScreen.setKeepOnScreenCondition {
            viewModel.isDarkTheme.value == null || viewModel.selectedLanguage.value == null
        }

        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

            if (isDarkTheme != null && selectedLanguage != null) {
                // If the language changed at runtime, recreate the activity to apply it globally
                LaunchedEffect(selectedLanguage) {
                    val currentLocale = resources.configuration.locales[0].language
                    if (selectedLanguage != currentLocale) {
                        recreate()
                    }
                }

                UnitWiseTheme(darkTheme = isDarkTheme!!) {
                    AppNavigation()
                }
            }
        }
    }
}
