package com.example.expensetracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.CategorySum
import com.example.expensetracker.data.model.DailySum
import com.example.expensetracker.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // ── Reads (reactive via Flow) ──────────────────────────────────────

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getByType(type: String): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE note LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%' 
        ORDER BY date DESC
    """)
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = :type")
    fun getTotalByType(type: String): Flow<Double>

    @Query("""
        SELECT category, SUM(amount) AS total 
        FROM transactions 
        WHERE type = :type 
        GROUP BY category 
        ORDER BY total DESC
    """)
    fun getCategorySums(type: String): Flow<List<CategorySum>>

    @Query("""
        SELECT date(date / 1000, 'unixepoch') AS day, SUM(amount) AS total 
        FROM transactions 
        WHERE type = :type AND date BETWEEN :startDate AND :endDate 
        GROUP BY day 
        ORDER BY day ASC
    """)
    fun getDailySums(startDate: Long, endDate: Long, type: String): Flow<List<DailySum>>

    /**
     * Returns distinct dates (as epoch millis) on which expenses occurred.
     * Used by the No-Spend Streak feature to find expense-free days.
     */
    @Query("""
        SELECT DISTINCT date(date / 1000, 'unixepoch') AS day 
        FROM transactions 
        WHERE type = 'EXPENSE'
        ORDER BY day DESC
    """)
    fun getExpenseDays(): Flow<List<String>>

    // ── Writes (suspend for coroutine safety) ──────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}
