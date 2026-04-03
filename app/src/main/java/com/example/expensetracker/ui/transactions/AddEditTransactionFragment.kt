package com.example.expensetracker.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.databinding.FragmentAddEditTransactionBinding
import com.example.expensetracker.util.Constants
import com.example.expensetracker.util.formatDate
import com.google.android.material.datepicker.MaterialDatePicker

class AddEditTransactionFragment : Fragment() {

    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditTransactionViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).transactionDao()
        AddEditTransactionViewModelFactory(TransactionRepository(dao))
    }

    private var transactionId: Long = -1L
    private var currentType: TransactionType = TransactionType.EXPENSE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionId = arguments?.getLong("transactionId", -1L) ?: -1L

        setupTypeToggle()
        setupCategoryDropdown()
        setupDatePicker()
        setupSaveButton()
        observeViewModel()

        // Load existing transaction for edit mode
        if (transactionId > 0) {
            binding.tvTitle.text = "Edit Transaction"
            binding.btnSave.text = "Update Transaction"
            viewModel.loadTransaction(transactionId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Type Toggle ────────────────────────────────────────────────

    private fun setupTypeToggle() {
        // Default to Expense checked
        binding.btnExpense.isChecked = true
        currentType = TransactionType.EXPENSE

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentType = if (checkedId == binding.btnIncome.id)
                    TransactionType.INCOME else TransactionType.EXPENSE
                updateCategoryDropdown()
            }
        }
    }

    // ── Category Dropdown ──────────────────────────────────────────

    private fun setupCategoryDropdown() {
        updateCategoryDropdown()
    }

    private fun updateCategoryDropdown() {
        val categories = if (currentType == TransactionType.INCOME)
            Constants.INCOME_CATEGORIES else Constants.EXPENSE_CATEGORIES

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        // Clear previous selection when type changes
        binding.actvCategory.setText("", false)
    }

    // ── Date Picker ────────────────────────────────────────────────

    private fun setupDatePicker() {
        // Show today by default
        binding.etDate.setText(System.currentTimeMillis().formatDate())

        binding.etDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(viewModel.selectedDate.value ?: System.currentTimeMillis())
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                viewModel.selectedDate.value = millis
                binding.etDate.setText(millis.formatDate())
            }
            picker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    // ── Save Button ────────────────────────────────────────────────

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val amountText = binding.etAmount.text?.toString().orEmpty()
            val category = binding.actvCategory.text?.toString().orEmpty()
            val note = binding.etNote.text?.toString().orEmpty()

            // Validation
            if (amountText.isBlank() || amountText.toDoubleOrNull() == null || amountText.toDouble() <= 0) {
                binding.tilAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }
            binding.tilAmount.error = null

            if (category.isBlank()) {
                binding.tilCategory.error = "Select a category"
                return@setOnClickListener
            }
            binding.tilCategory.error = null

            viewModel.save(
                existingId = transactionId,
                amount = amountText.toDouble(),
                type = currentType,
                category = category,
                note = note
            )
        }
    }

    // ── Observe ────────────────────────────────────────────────────

    private fun observeViewModel() {
        // Pre-populate form when editing
        viewModel.transaction.observe(viewLifecycleOwner) { txn ->
            txn ?: return@observe
            binding.etAmount.setText(txn.amount.toString())
            binding.etNote.setText(txn.note)
            binding.actvCategory.setText(txn.category, false)
            binding.etDate.setText(txn.date.formatDate())

            if (txn.type == TransactionType.INCOME) {
                binding.btnIncome.isChecked = true
            } else {
                binding.btnExpense.isChecked = true
            }
            currentType = txn.type
            updateCategoryDropdown()
            binding.actvCategory.setText(txn.category, false)
        }

        // Navigate back after save
        viewModel.saved.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(requireContext(), "Transaction saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}
