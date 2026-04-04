package com.example.expensetracker.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Goal
import com.example.expensetracker.data.repository.GoalRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AddEditGoalViewModel(private val repository: GoalRepository) : ViewModel() {

    private val _goal = MutableLiveData<Goal?>()
    val goal: LiveData<Goal?> = _goal

    val selectedDeadline = MutableLiveData(System.currentTimeMillis())

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean> = _saved

    fun loadGoal(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            val loadedGoal = repository.getGoalById(id).firstOrNull()
            _goal.value = loadedGoal
            loadedGoal?.let { selectedDeadline.value = it.deadline }
        }
    }

    fun save(
        existingId: Long,
        title: String,
        targetAmount: Double,
        savedAmount: Double
    ) {
        viewModelScope.launch {
            val deadline = selectedDeadline.value ?: System.currentTimeMillis()
            val newGoal = Goal(
                id = if (existingId > 0) existingId else 0,
                title = title,
                targetAmount = targetAmount,
                savedAmount = savedAmount,
                deadline = deadline
            )
            if (existingId > 0) repository.update(newGoal) else repository.insert(newGoal)
            _saved.value = true
        }
    }
}
