package com.example.expensetracker.data.model

/**
 * Lightweight POJO for Room query projection.
 * Holds the sum of amounts grouped by category — used for pie charts and insights.
 */
data class CategorySum(
    val category: String,
    val total: Double
)
