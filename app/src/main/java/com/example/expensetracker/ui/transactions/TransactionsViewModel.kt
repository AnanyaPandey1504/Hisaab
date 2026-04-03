package com.example.expensetracker.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {

    // ── Filter state ───────────────────────────────────────────────

    val searchQuery = MutableStateFlow("")
    val typeFilter = MutableStateFlow<String?>(null)   // null = "All"

    // ── Combined reactive query ────────────────────────────────────

    val transactions: LiveData<List<Transaction>> =
        combine(searchQuery, typeFilter) { query, type ->
            Pair(query, type)
        }.flatMapLatest { (query, type) ->
            when {
                query.isNotBlank() -> repository.searchTransactions(query)
                type != null       -> repository.getByType(type)
                else               -> repository.getAllTransactions()
            }
        }.asLiveData()

    // ── Actions ────────────────────────────────────────────────────

    fun setSearchQuery(query: String) { searchQuery.value = query }
    fun setTypeFilter(type: String?)  { typeFilter.value = type }

    fun delete(transaction: Transaction) {
        viewModelScope.launch { repository.delete(transaction) }
    }
}
