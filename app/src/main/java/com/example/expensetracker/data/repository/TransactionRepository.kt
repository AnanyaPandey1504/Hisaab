package com.example.expensetracker.data.repository

import com.example.expensetracker.data.db.TransactionDao
import com.example.expensetracker.data.model.CategorySum
import com.example.expensetracker.data.model.DailySum
import com.example.expensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for transaction data.
 *
 * ViewModels depend on this class instead of the DAO directly.
 * This makes it easy to swap out the data source (e.g. add a remote API)
 * without changing any ViewModel code.
 */
class TransactionRepository(private val dao: TransactionDao) {

    // ── Reactive reads ─────────────────────────────────────────────────

    fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()

    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        dao.getByDateRange(startDate, endDate)

    fun getByType(type: String): Flow<List<Transaction>> = dao.getByType(type)

    fun searchTransactions(query: String): Flow<List<Transaction>> =
        dao.searchTransactions(query)

    fun getTotalByType(type: String): Flow<Double> = dao.getTotalByType(type)

    fun getCategorySums(type: String): Flow<List<CategorySum>> = dao.getCategorySums(type)

    fun getDailySums(startDate: Long, endDate: Long, type: String): Flow<List<DailySum>> =
        dao.getDailySums(startDate, endDate, type)

    fun getExpenseDays(): Flow<List<String>> = dao.getExpenseDays()

    // ── Writes ─────────────────────────────────────────────────────────

    suspend fun insert(transaction: Transaction): Long = dao.insert(transaction)

    suspend fun update(transaction: Transaction) = dao.update(transaction)

    suspend fun delete(transaction: Transaction) = dao.delete(transaction)
}
