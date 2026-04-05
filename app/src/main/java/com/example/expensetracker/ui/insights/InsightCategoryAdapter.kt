package com.example.expensetracker.ui.insights

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.data.model.CategorySum
import com.example.expensetracker.databinding.ItemInsightCategoryBinding
import com.example.expensetracker.util.CategoryUtils
import com.example.expensetracker.util.formatCurrency
import kotlin.math.roundToInt

class InsightCategoryAdapter : ListAdapter<CategorySum, InsightCategoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var totalSum: Double = 0.0

    fun updateDataAndTotal(list: List<CategorySum>, total: Double) {
        totalSum = total
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInsightCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemInsightCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategorySum) {
            binding.tvIcon.text = CategoryUtils.getIcon(item.category)
            binding.tvCategory.text = item.category
            binding.tvAmount.text = item.total.formatCurrency()

            val percentage = if (totalSum > 0) {
                ((item.total / totalSum) * 100).roundToInt()
            } else 0

            binding.tvPercentage.text = "$percentage%"
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategorySum>() {
            override fun areItemsTheSame(old: CategorySum, new: CategorySum) = old.category == new.category
            override fun areContentsTheSame(old: CategorySum, new: CategorySum) = old == new
        }
    }
}
