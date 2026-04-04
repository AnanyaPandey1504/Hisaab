package com.example.expensetracker.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Goal
import com.example.expensetracker.data.repository.GoalRepository
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // ── Goals List ─────────────────────────────────────────────────

    val goals: LiveData<List<Goal>> = goalRepository.getAllGoals().asLiveData()

    fun delete(goal: Goal) {
        viewModelScope.launch { goalRepository.delete(goal) }
    }

    // ── No-Spend Streak Algorithm ──────────────────────────────────

    /**
     * A "No-Spend Streak" is the number of consecutive days, ending today or yesterday,
     * where the user has zero EXPENSE transactions.
     */
    val streakData: LiveData<Pair<Int, Int>> = transactionRepository.getExpenseDays()
        .map { expenseDates ->
            calculateStreaks(expenseDates)
        }
        .asLiveData()

    private fun calculateStreaks(expenseDates: List<String>): Pair<Int, Int> {
        if (expenseDates.isEmpty()) return Pair(0, 0) // Not enough data or perfect streak? Fallback to 0.

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        // Convert to epoch days for easy math
        val expenseDays = expenseDates.mapNotNull { dateStr ->
            sdf.parse(dateStr)?.let { TimeUnit.MILLISECONDS.toDays(it.time) }
        }.sortedDescending()

        val todayMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val today = TimeUnit.MILLISECONDS.toDays(todayMillis)

        var currentStreak = 0
        var bestStreak = 0

        // 1. Calculate current streak
        // If today has expenses, streak is 0.
        // If yesterday has expenses but today doesn't, streak is 1.
        if (expenseDays.contains(today)) {
            currentStreak = 0
        } else {
            // Find the most recent expense day
            val lastExpenseDay = expenseDays.firstOrNull { it < today }
            currentStreak = if (lastExpenseDay != null) {
                (today - lastExpenseDay - 1).toInt().coerceAtLeast(0)
            } else {
                // No expenses ever before today? Unlikely, but handle it.
                0
            }
            
            // Allow today to count if it's currently without expense
            currentStreak++
        }

        // 2. Calculate best streak
        if (expenseDays.size >= 2) {
            for (i in 0 until expenseDays.size - 1) {
                val gap = (expenseDays[i] - expenseDays[i + 1] - 1).toInt()
                if (gap > bestStreak) bestStreak = gap
            }
        }
        
        // The current streak might be the best one
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }

        return Pair(currentStreak, bestStreak)
    }
}
