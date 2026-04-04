package com.example.expensetracker.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.data.db.AppDatabase
import com.example.expensetracker.data.repository.GoalRepository
import com.example.expensetracker.databinding.FragmentAddEditGoalBinding
import com.example.expensetracker.util.formatDate
import com.google.android.material.datepicker.MaterialDatePicker

class AddEditGoalFragment : Fragment() {

    private var _binding: FragmentAddEditGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditGoalViewModel by viewModels {
        val dao = AppDatabase.getInstance(requireContext()).goalDao()
        AddEditGoalViewModelFactory(GoalRepository(dao))
    }

    private var goalId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalId = arguments?.getLong("goalId", -1L) ?: -1L

        setupDatePicker()
        setupSaveButton()
        observeViewModel()

        if (goalId > 0) {
            binding.tvScreenTitle.text = "Edit Goal"
            binding.btnSave.text = "Update Goal"
            viewModel.loadGoal(goalId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDatePicker() {
        binding.etDeadline.setText(System.currentTimeMillis().formatDate())

        binding.etDeadline.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select deadline")
                .setSelection(viewModel.selectedDeadline.value ?: System.currentTimeMillis())
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                viewModel.selectedDeadline.value = millis
                binding.etDeadline.setText(millis.formatDate())
            }
            picker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text?.toString().orEmpty()
            val targetStr = binding.etTarget.text?.toString().orEmpty()
            val savedStr = binding.etSaved.text?.toString().orEmpty()

            if (title.isBlank()) {
                binding.tilTitle.error = "Title required"
                return@setOnClickListener
            } else binding.tilTitle.error = null

            val targetValid = targetStr.toDoubleOrNull()
            if (targetValid == null || targetValid <= 0) {
                binding.tilTarget.error = "Valid target amount required"
                return@setOnClickListener
            } else binding.tilTarget.error = null

            val savedValid = savedStr.toDoubleOrNull() ?: 0.0

            viewModel.save(
                existingId = goalId,
                title = title,
                targetAmount = targetValid,
                savedAmount = savedValid
            )
        }
    }

    private fun observeViewModel() {
        viewModel.goal.observe(viewLifecycleOwner) { goal ->
            goal ?: return@observe
            binding.etTitle.setText(goal.title)
            binding.etTarget.setText(goal.targetAmount.toString())
            binding.etSaved.setText(goal.savedAmount.toString())
            binding.etDeadline.setText(goal.deadline.formatDate())
        }

        viewModel.saved.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(requireContext(), "Goal saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}
