package com.example.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val inrLocale: Locale = Locale.forLanguageTag("en-IN")

    private val inrFormat: NumberFormat = NumberFormat.getCurrencyInstance(inrLocale).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    private val inrWithDecimals: NumberFormat = NumberFormat.getCurrencyInstance(inrLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun format(amount: Double): String {
        return try {
            val formatted = if (amount % 1.0 == 0.0) {
                inrFormat.format(amount)
            } else {
                inrWithDecimals.format(amount)
            }
            normalizeRupeeSymbol(formatted)
        } catch (e: Exception) {
            "₹" + String.format(Locale.US, "%,.0f", amount)
        }
    }

    private fun normalizeRupeeSymbol(formatted: String): String {
        return formatted
            .replace("INR", "₹")
            .replace("Rs.", "₹")
            .replace("Rs", "₹")
            .trim()
    }
}
