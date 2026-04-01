package com.example.expensetracker.data.model

/**
 * Lightweight POJO for Room query projection.
 * Holds the sum of amounts for a single day — used for weekly trend bar charts.
 *
 * [day] is formatted as "yyyy-MM-dd" string produced by SQLite's date() function.
 */
data class DailySum(
    val day: String,
    val total: Double
)
