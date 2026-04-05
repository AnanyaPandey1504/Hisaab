package com.example.expensetracker.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.content.res.Configuration
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.expensetracker.R
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.databinding.FragmentHomeBinding
import com.example.expensetracker.util.formatCurrency
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Manual DI: create repository from DB singleton, then create ViewModel via factory
    private val viewModel: HomeViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        HomeViewModelFactory(TransactionRepository(dao))
    }

    // ── Chart color palette ────────────────────────────────────────

    private val chartColors by lazy {
        listOf(
            R.color.chart_1, R.color.chart_2, R.color.chart_3, R.color.chart_4,
            R.color.chart_5, R.color.chart_6, R.color.chart_7, R.color.chart_8
        ).map { ContextCompat.getColor(requireContext(), it) }
    }

    // ── Lifecycle ──────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeToggle()
        setupPieChart()
        setupBarChart()
        observeViewModel()
    }
    
    private fun setupThemeToggle() {
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        binding.btnThemeToggle.setImageResource(if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon)
        
        binding.btnThemeToggle.setOnClickListener {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // avoid memory leak
    }

    // ── Observe ViewModel ──────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = (balance ?: 0.0).formatCurrency()
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvIncome.text = (income ?: 0.0).formatCurrency()
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.tvExpenses.text = (expenses ?: 0.0).formatCurrency()
        }

        viewModel.categorySums.observe(viewLifecycleOwner) { sums ->
            updatePieChart(sums.orEmpty())
        }

        viewModel.weeklySums.observe(viewLifecycleOwner) { sums ->
            updateBarChart(sums.orEmpty())
        }
    }

    // ── Pie Chart (Spending by Category) ───────────────────────────

    private fun setupPieChart() {
        val textColor = com.google.android.material.color.MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorOnSurface)

        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 45f
            setDrawCenterText(true)
            centerText = "Expenses"
            setCenterTextSize(14f)
            setCenterTextColor(textColor)
            setEntryLabelTextSize(11f)
            setEntryLabelColor(textColor)
            legend.isEnabled = true
            legend.textColor = textColor
            legend.textSize = 11f
            setNoDataText("No expenses yet")
            setNoDataTextColor(textColor)
            animateY(600)
        }
    }

    private fun updatePieChart(sums: List<com.example.expensetracker.data.model.CategorySum>) {
        if (sums.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.invalidate()
            return
        }

        val textColor = com.google.android.material.color.MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorOnSurface)
        val entries = sums.map { PieEntry(it.total.toFloat(), it.category) }
        val dataSet = PieDataSet(entries, "").apply {
            colors = chartColors
            valueTextSize = 12f
            valueTextColor = textColor
            valueFormatter = PercentFormatter(binding.pieChart)
            sliceSpace = 2f
        }
        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }

    // ── Bar Chart (Weekly Trend) ───────────────────────────────────

    private fun setupBarChart() {
        val textColor = com.google.android.material.color.MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorOnSurface)

        binding.barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            legend.isEnabled = false
            setNoDataText("No expenses this week")
            setNoDataTextColor(textColor)
            animateY(600)

            // X axis: day labels at bottom
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.textColor = textColor

            // Y axis
            axisLeft.axisMinimum = 0f
            axisLeft.setDrawGridLines(true)
            axisLeft.textColor = textColor
            axisRight.isEnabled = false
        }
    }

    private fun updateBarChart(sums: List<com.example.expensetracker.data.model.DailySum>) {
        if (sums.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.invalidate()
            return
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFormat = SimpleDateFormat("EEE", Locale.US)

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        sums.forEachIndexed { index, dailySum ->
            entries.add(BarEntry(index.toFloat(), dailySum.total.toFloat()))
            try {
                val date = inputFormat.parse(dailySum.day)
                labels.add(if (date != null) dayFormat.format(date) else dailySum.day)
            } catch (_: Exception) {
                labels.add(dailySum.day)
            }
        }

        val textColor = com.google.android.material.color.MaterialColors.getColor(requireView(), com.google.android.material.R.attr.colorOnSurface)
        val dataSet = BarDataSet(entries, "Expenses").apply {
            color = ContextCompat.getColor(requireContext(), R.color.chart_1)
            valueTextSize = 10f
            valueTextColor = textColor
        }

        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.data = BarData(dataSet).apply { barWidth = 0.6f }
        binding.barChart.invalidate()
    }
}
