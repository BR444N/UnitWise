package com.br444n.unitwise.app.ui.util

import java.util.Locale

object LocaleHelper {
    fun normalizeLanguageCode(languageCode: String): String =
        languageCode
            .substringBefore('-')
            .ifBlank { Locale.ENGLISH.language }
            .lowercase(Locale.ROOT)
}
