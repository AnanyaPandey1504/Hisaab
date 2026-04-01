package com.example.expensetracker.data.db

import androidx.room.TypeConverter
import com.example.expensetracker.data.model.TransactionType

/**
 * Room type converters for non-primitive types.
 *
 * We store [TransactionType] as its enum name (String) rather than ordinal (Int)
 * so that reordering enum values in the future won't corrupt existing data.
 */
class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}
