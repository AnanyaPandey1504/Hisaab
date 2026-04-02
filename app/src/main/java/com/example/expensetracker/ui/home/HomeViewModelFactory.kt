package com.example.expensetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.repository.TransactionRepository

/**
 * Factory that creates [HomeViewModel] with a [TransactionRepository] dependency.
 *
 * Required because ViewModelProvider cannot inject constructor arguments by default.
 * This is "manual DI" — an alternative to using Hilt/Dagger.
 */
class HomeViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
