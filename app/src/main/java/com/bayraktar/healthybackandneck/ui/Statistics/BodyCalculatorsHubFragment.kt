package com.bayraktar.healthybackandneck.ui.Statistics

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentBodyCalculatorsHubBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat

@AndroidEntryPoint
class BodyCalculatorsHubFragment : Fragment(R.layout.fragment_body_calculators_hub) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBodyCalculatorsHubBinding.bind(view)
        val model: ProgressViewModel by viewModels()
        binding.goBack.setOnClickListener { findNavController().popBackStack() }
        listOf(binding.editEndeks to R.id.BMIFragment, binding.editDayofCalorie to R.id.dailyCalorieFragment,
            binding.editfatrate to R.id.bodyFatRateFragment, binding.bmiAction to R.id.BMIFragment,
            binding.calorieAction to R.id.dailyCalorieFragment, binding.fatAction to R.id.bodyFatRateFragment).forEach { (action, target) ->
            action.setOnClickListener { findNavController().navigate(target) }
        }
        model.calculators.observe(viewLifecycleOwner) { value ->
            renderSaved(binding.bmiSaved, value.validSavedBmi, R.string.pc_bmi_unit, 2)
            renderSaved(binding.calorieSaved, value.validSavedCalories, R.string.pc_kcal_unit, 0)
            renderSaved(binding.fatSaved, value.validSavedFat, R.string.pc_percent_unit, 1)
        }
    }
    private fun renderSaved(label: TextView, value: Double?, unit: Int, digits: Int) {
        label.text = if (value == null) getString(R.string.ac_no_saved) else getString(R.string.pc_saved_estimate,
            NumberFormat.getNumberInstance().apply { maximumFractionDigits = digits }.format(value) + " " + getString(unit))
    }
}
