package com.example.expensetracker.data.repository

import com.example.expensetracker.data.db.GoalDao
import com.example.expensetracker.data.model.Goal
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for goal data.
 */
class GoalRepository(private val dao: GoalDao) {

    fun getAllGoals(): Flow<List<Goal>> = dao.getAllGoals()

    fun getGoalById(id: Long): Flow<Goal?> = dao.getGoalById(id)

    suspend fun insert(goal: Goal): Long = dao.insert(goal)

    suspend fun update(goal: Goal) = dao.update(goal)

    suspend fun delete(goal: Goal) = dao.delete(goal)
}
