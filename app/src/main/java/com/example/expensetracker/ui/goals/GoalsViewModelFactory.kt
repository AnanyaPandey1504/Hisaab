package com.example.expensetracker.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.repository.GoalRepository
import com.example.expensetracker.data.repository.TransactionRepository

class GoalsViewModelFactory(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            return GoalsViewModel(goalRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
