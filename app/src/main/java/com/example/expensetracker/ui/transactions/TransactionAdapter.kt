package com.example.expensetracker.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.databinding.ItemTransactionBinding
import com.example.expensetracker.util.CategoryUtils
import com.example.expensetracker.util.formatCurrency
import com.example.expensetracker.util.formatDate

/**
 * Efficient RecyclerView adapter using ListAdapter + DiffUtil.
 * DiffUtil computes list differences on a background thread so only
 * changed items are re-bound — much better than notifyDataSetChanged().
 */
class TransactionAdapter(
    private val onClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(getItem(pos))
            }
        }

        fun bind(txn: Transaction) {
            binding.tvIcon.text = CategoryUtils.getIcon(txn.category)
            binding.tvCategory.text = txn.category
            binding.tvNote.text = txn.note.ifBlank { txn.type.name.lowercase().replaceFirstChar { it.uppercase() } }
            binding.tvDate.text = txn.date.formatDate()

            val amountColor = if (txn.type == TransactionType.INCOME)
                ContextCompat.getColor(binding.root.context, R.color.income_green)
            else
                ContextCompat.getColor(binding.root.context, R.color.expense_red)

            val prefix = if (txn.type == TransactionType.INCOME) "+ " else "- "
            binding.tvAmount.text = prefix + txn.amount.formatCurrency()
            binding.tvAmount.setTextColor(amountColor)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
            override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
        }
    }
}
