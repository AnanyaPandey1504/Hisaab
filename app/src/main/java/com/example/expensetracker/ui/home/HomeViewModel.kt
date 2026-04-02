package com.example.expensetracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.expensetracker.data.model.CategorySum
import com.example.expensetracker.data.model.DailySum
import com.example.expensetracker.data.repository.TransactionRepository
import java.util.Calendar

class HomeViewModel(private val repository: TransactionRepository) : ViewModel() {

    // ── Raw totals from DB ─────────────────────────────────────────

    val totalIncome: LiveData<Double> = repository.getTotalByType("INCOME").asLiveData()
    val totalExpenses: LiveData<Double> = repository.getTotalByType("EXPENSE").asLiveData()

    // ── Derived balance (income − expenses) ────────────────────────

    val balance: LiveData<Double> = MediatorLiveData<Double>().apply {
        fun update() {
            val income = totalIncome.value ?: 0.0
            val expenses = totalExpenses.value ?: 0.0
            value = income - expenses
        }
        addSource(totalIncome) { update() }
        addSource(totalExpenses) { update() }
    }

    // ── Pie chart data: spending by category ───────────────────────

    val categorySums: LiveData<List<CategorySum>> =
        repository.getCategorySums("EXPENSE").asLiveData()

    // ── Bar chart data: last 7 days' expense totals ────────────────

    val weeklySums: LiveData<List<DailySum>> by lazy {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -6)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        repository.getDailySums(startDate, endDate, "EXPENSE").asLiveData()
    }
}
