package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val deadline: Long,   // epoch millis
    val createdAt: Long = System.currentTimeMillis()
)
