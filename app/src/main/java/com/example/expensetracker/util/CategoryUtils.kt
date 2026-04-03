package com.example.expensetracker.util

/**
 * Maps transaction category names to emoji icons for display in lists.
 */
object CategoryUtils {

    private val CATEGORY_ICONS = mapOf(
        // Expense categories
        "Food" to "🍔",
        "Transport" to "🚗",
        "Shopping" to "🛍️",
        "Bills" to "📄",
        "Entertainment" to "🎮",
        "Health" to "💊",
        "Education" to "📚",
        // Income categories
        "Salary" to "💰",
        "Freelance" to "💻",
        "Investment" to "📈",
        "Gift" to "🎁",
        // Fallback
        "Other" to "📌"
    )

    fun getIcon(category: String): String =
        CATEGORY_ICONS[category] ?: "📌"
}
