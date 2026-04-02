package com.example.expensetracker.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a Double as Indian Rupee currency string.
 * Example: 125000.5 → "₹1,25,000.50"
 */
fun Double.formatCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return formatter.format(this)
}
