package com.example.expensetracker.ui.insights

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.expensetracker.data.model.CategorySum
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // Toggle state (default EXPENSE)
    val transactionType = MutableStateFlow(TransactionType.EXPENSE)

    // Data mapped directly from ROOM based on toggle state
    val categoryData: LiveData<List<CategorySum>> = transactionType
        .flatMapLatest { type ->
            repository.getCategorySums(type.name)
        }
        .asLiveData()

    // Derived total sum
    val totalAmount: LiveData<Double> = transactionType
        .flatMapLatest { type ->
            repository.getCategorySums(type.name).map { list ->
                list.sumOf { it.total }
            }
        }
        .asLiveData()

    fun setTransactionType(type: TransactionType) {
        transactionType.value = type
    }
}
