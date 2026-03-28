package com.br444n.unitwise.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.br444n.unitwise.app.navigation.AppNavigation
import com.br444n.unitwise.app.ui.MainViewModel
import com.br444n.unitwise.app.ui.theme.UnitWiseTheme
import com.br444n.unitwise.app.ui.util.LocaleHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val initialLanguage = LocaleHelper.normalizeLanguageCode(
            prefs.getString("selected_language", "en") ?: "en"
        )
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(initialLanguage)
        )

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
                LaunchedEffect(selectedLanguage) {
                    val targetLanguageCode = LocaleHelper.normalizeLanguageCode(selectedLanguage!!)
                    val currentLanguageCode = LocaleHelper.normalizeLanguageCode(
                        AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    )

                    if (targetLanguageCode != currentLanguageCode) {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(targetLanguageCode)
                        )
                    }
                }

                UnitWiseTheme(darkTheme = isDarkTheme!!) {
                    AppNavigation()
                }
            }
        }
    }
}
