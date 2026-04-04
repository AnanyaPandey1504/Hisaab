package com.example.expensetracker.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.model.Goal
import com.example.expensetracker.databinding.ItemGoalBinding
import com.example.expensetracker.util.formatCurrency
import com.example.expensetracker.util.formatDate
import kotlin.math.roundToInt

class GoalAdapter(
    private val onClick: (Goal) -> Unit
) : ListAdapter<Goal, GoalAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var currentStreak: Int = 0
    private var bestStreak: Int = 0

    fun updateStreak(current: Int, best: Int) {
        currentStreak = current
        bestStreak = best
        if (currentList.isNotEmpty()) {
            notifyItemChanged(0) // Header is attached to position 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == 0)
    }

    inner class ViewHolder(
        val binding: ItemGoalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.goalCard.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(getItem(pos))
            }
        }

        fun bind(goal: Goal, isFirst: Boolean) {
            // Header visibility and logic
            if (isFirst) {
                binding.headerLayout.visibility = android.view.View.VISIBLE
                binding.tvCurrentStreak.text = "Current: $currentStreak days"
                binding.tvBestStreak.text = "Best: $bestStreak days"
                
                binding.tvStreakMessage.text = when {
                    currentStreak == 0 -> "Buy nothing today to start a streak!"
                    currentStreak >= bestStreak && currentStreak > 0 -> "New record! Keep it up!"
                    currentStreak >= 3 -> "You're on fire! 🔥"
                    else -> "Spend less, save more!"
                }
            } else {
                binding.headerLayout.visibility = android.view.View.GONE
            }

            // Goal details
            binding.tvTitle.text = goal.title
            
            val formattedSaved = goal.savedAmount.formatCurrency()
            val formattedTarget = goal.targetAmount.formatCurrency()
            binding.tvAmountProgress.text = "$formattedSaved / $formattedTarget"

            val percentage = if (goal.targetAmount > 0) {
                ((goal.savedAmount / goal.targetAmount) * 100).roundToInt()
            } else 0
            
            // Progress bar
            binding.progressBar.progress = percentage.coerceIn(0, 100)
            binding.tvPercentage.text = "${percentage.coerceIn(0, 100)}%"
            
            binding.tvDeadline.text = "Deadline: ${goal.deadline.formatDate()}"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Goal>() {
            override fun areItemsTheSame(old: Goal, new: Goal) = old.id == new.id
            override fun areContentsTheSame(old: Goal, new: Goal) = old == new
        }
    }
}
