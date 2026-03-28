package com.br444n.unitwise.app.ui.util

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {
    fun wrap(context: Context, languageCode: String): Context {
        val normalizedLanguageCode = normalizeLanguageCode(languageCode)
        val locale = Locale.forLanguageTag(normalizedLanguageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        configuration.setLocales(LocaleList(locale))

        return context.createConfigurationContext(configuration)
    }

    fun currentLanguageCode(configuration: Configuration): String {
        val locale = configuration.locales[0]
        return normalizeLanguageCode(locale?.toLanguageTag().orEmpty())
    }

    fun normalizeLanguageCode(languageCode: String): String {
        return languageCode
            .substringBefore('-')
            .ifBlank { Locale.ENGLISH.language }
            .lowercase(Locale.ROOT)
    }
}
