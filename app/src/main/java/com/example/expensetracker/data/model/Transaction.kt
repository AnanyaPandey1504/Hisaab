package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,       // epoch millis (UTC)
    val note: String = ""
)
