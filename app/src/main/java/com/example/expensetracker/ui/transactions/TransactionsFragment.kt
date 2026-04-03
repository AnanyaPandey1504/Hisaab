package com.example.expensetracker.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        TransactionsViewModelFactory(TransactionRepository(dao))
    }

    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupChips()
        setupFab()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── RecyclerView + Swipe-to-delete ─────────────────────────────

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            // Navigate to edit screen
            val bundle = bundleOf("transactionId" to transaction.id)
            findNavController().navigate(R.id.action_transactions_to_addEdit, bundle)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Swipe-to-delete
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.delete(adapter.currentList[position])
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }

    // ── Search ─────────────────────────────────────────────────────

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }
    }

    // ── Type Filter Chips ──────────────────────────────────────────

    private fun setupChips() {
        binding.chipAll.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when {
                checkedIds.contains(R.id.chipIncome) -> "INCOME"
                checkedIds.contains(R.id.chipExpense) -> "EXPENSE"
                else -> null
            }
            viewModel.setTypeFilter(filter)
        }
    }

    // ── FAB ────────────────────────────────────────────────────────

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val bundle = bundleOf("transactionId" to -1L)
            findNavController().navigate(R.id.action_transactions_to_addEdit, bundle)
        }
    }

    // ── Observe ────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyState.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }
}
