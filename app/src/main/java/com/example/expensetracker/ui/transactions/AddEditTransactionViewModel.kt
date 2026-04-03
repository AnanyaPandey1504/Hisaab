package com.example.expensetracker.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddEditTransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // ── Editing state ──────────────────────────────────────────────

    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    val selectedDate = MutableLiveData(System.currentTimeMillis())

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean> = _saved

    // ── Load for edit ──────────────────────────────────────────────

    fun loadTransaction(id: Long) {
        if (id <= 0) return   // "create new" mode — nothing to load
        viewModelScope.launch {
            val all = repository.getAllTransactions().firstOrNull() ?: emptyList()
            _transaction.value = all.find { it.id == id }
            _transaction.value?.let { selectedDate.value = it.date }
        }
    }

    // ── Save (insert or update) ────────────────────────────────────

    fun save(
        existingId: Long,
        amount: Double,
        type: TransactionType,
        category: String,
        note: String
    ) {
        viewModelScope.launch {
            val date = selectedDate.value ?: System.currentTimeMillis()
            val txn = Transaction(
                id = if (existingId > 0) existingId else 0,
                amount = amount,
                type = type,
                category = category,
                date = date,
                note = note
            )
            if (existingId > 0) repository.update(txn) else repository.insert(txn)
            _saved.value = true
        }
    }
}
