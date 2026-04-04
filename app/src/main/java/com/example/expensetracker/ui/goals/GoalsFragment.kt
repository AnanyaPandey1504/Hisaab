package com.example.expensetracker.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.repository.GoalRepository
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.databinding.FragmentGoalsBinding

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val goalDao = appDatabase.goalDao()
        val transactionDao = appDatabase.transactionDao()
        GoalsViewModelFactory(GoalRepository(goalDao), TransactionRepository(transactionDao))
    }

    private lateinit var adapter: GoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = GoalAdapter { goal ->
            val bundle = bundleOf("goalId" to goal.id)
            findNavController().navigate(R.id.action_goals_to_addEdit, bundle)
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

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val bundle = bundleOf("goalId" to -1L)
            findNavController().navigate(R.id.action_goals_to_addEdit, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.goals.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyState.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.streakData.observe(viewLifecycleOwner) { (current, best) ->
            adapter.updateStreak(current, best)
        }
    }
}
