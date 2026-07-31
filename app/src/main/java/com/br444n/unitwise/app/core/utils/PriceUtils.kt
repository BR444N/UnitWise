package com.br444n.unitwise.app.core.utils

import java.text.NumberFormat
import java.util.Locale

object PriceUtils {
    /**
     * Parses a string containing a price (e.g., "$15.43", "15,43", "1,500.00")
     * into a Double. Returns 0.0 if parsing fails.
     */
    fun parsePrice(priceString: String): Double {
        if (priceString.isBlank()) return 0.0

        // Remove everything except digits, dots and commas
        val cleanedString = priceString.replace(Regex("[^\\d.,]"), "")
        if (cleanedString.isEmpty()) return 0.0

        // Handle commas vs dots. If we have something like "1,500.00", we want to remove the comma.
        // If we have something like "15,43" (European style), we want to replace comma with dot.

        // A simple heuristic: if there's both a comma and a dot, remove the one that appears first or the comma.
        // For simplicity, assuming US format for now, or just removing commas if a dot exists.
        val hasDot = cleanedString.contains(".")
        val hasComma = cleanedString.contains(",")

        var parseableString = cleanedString
        if (hasDot && hasComma) {
            parseableString = parseableString.replace(",", "")
        } else if (hasComma) {
            parseableString = parseableString.replace(",", ".")
        }

        return parseableString.toDoubleOrNull() ?: 0.0
    }

    /**
     * Formats a Double into a standard currency string (e.g., "$15.43")
     */
    fun formatPrice(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(value)
    }
}
