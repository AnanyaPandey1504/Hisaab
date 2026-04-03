package com.example.expensetracker.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formats epoch millis as a human-readable date string.
 * Example: 1712044200000 → "02 Apr 2026"
 */
fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}
