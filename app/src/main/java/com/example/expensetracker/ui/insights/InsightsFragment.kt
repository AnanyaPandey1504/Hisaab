package com.example.expensetracker.ui.insights

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetracker.R
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.databinding.FragmentInsightsBinding
import com.example.expensetracker.util.formatCurrency
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InsightsViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        InsightsViewModelFactory(TransactionRepository(dao))
    }

    private lateinit var adapter: InsightCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggle()
        setupPieChart()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToggle() {
        binding.btnExpense.isChecked = true

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = if (checkedId == binding.btnExpense.id) TransactionType.EXPENSE else TransactionType.INCOME
                binding.tvTotalLabel.text = if (type == TransactionType.EXPENSE) "Total Expense" else "Total Income"
                viewModel.setTransactionType(type)
            }
        }
    }

    private fun setupPieChart() {
        val textColor = com.google.android.material.color.MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorOnSurface)

        binding.pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Distribution"
            setCenterTextColor(textColor)
            setEntryLabelColor(textColor)
            setEntryLabelTextSize(12f)
            legend.isEnabled = false // We use the recyclerview as a legend
        }
    }

    private fun setupRecyclerView() {
        adapter = InsightCategoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            val safeTotal = total ?: 0.0
            binding.tvTotalAmount.text = safeTotal.formatCurrency()

            // When total changes, the category list needs re-rendering for %
            val currentList = viewModel.categoryData.value ?: emptyList()
            adapter.updateDataAndTotal(currentList, safeTotal)
        }

        viewModel.categoryData.observe(viewLifecycleOwner) { list ->
            val total = viewModel.totalAmount.value ?: 0.0
            adapter.updateDataAndTotal(list, total)

            if (list.isNullOrEmpty()) {
                binding.pieChart.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.pieChart.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE

                val entries = list.map { PieEntry(it.total.toFloat(), it.category) }
                val dataSet = PieDataSet(entries, "Categories").apply {
                    val colorsArray = listOf(
                        ContextCompat.getColor(requireContext(), R.color.chart_1),
                        ContextCompat.getColor(requireContext(), R.color.chart_2),
                        ContextCompat.getColor(requireContext(), R.color.chart_3),
                        ContextCompat.getColor(requireContext(), R.color.chart_4),
                        ContextCompat.getColor(requireContext(), R.color.chart_5),
                        ContextCompat.getColor(requireContext(), R.color.chart_6),
                        ContextCompat.getColor(requireContext(), R.color.chart_7),
                        ContextCompat.getColor(requireContext(), R.color.chart_8)
                    )
                    colors = colorsArray
                    sliceSpace = 3f
                    selectionShift = 5f
                    // Text coloring for values inside the pie
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                }

                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.invalidate()
                binding.pieChart.animateY(1000)
            }
        }
    }
}
