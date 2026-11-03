package com.bayraktar.healthybackandneck.ui.Breath

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentBreathBinding
import com.bayraktar.healthybackandneck.databinding.ItemBreathTechniqueBinding

/** The existing breathing guide is informational; no simulated timer or inactive start action. */
class BreathFragment : Fragment() {
    private var _binding: FragmentBreathBinding? = null
    private val binding get() = _binding!!
    private val rows = mutableListOf<ItemBreathTechniqueBinding>()
    private var expanded = 0

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        expanded = state?.getInt("expandedTechnique", 0) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentBreathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        val techniques = listOf(
            R.string.diyafram_title to R.string.diyafram_desc,
            R.string.four_breath to R.string.fourbreath_desc,
            R.string.counting_breath to R.string.countingbreath_desc,
            R.string.focus_breath to R.string.focus_breath_desc,
            R.string.closed_breath to R.string.closed_breath_desc,
            R.string.alternatv_nose_breath to R.string.alternativ_breath_desc,
            R.string.seyir_breath to R.string.seyir_desc,
            R.string.medidation_breath to R.string.mediationbreath_desc,
        )
        techniques.forEachIndexed { index, (title, description) ->
            val row = ItemBreathTechniqueBinding.inflate(layoutInflater, binding.techniqueList, false)
            row.techniqueNumber.text = String.format(java.util.Locale.getDefault(), "%02d", index + 1)
            row.techniqueTitle.setText(title)
            row.techniqueDescription.setText(description)
            row.techniqueHeader.contentDescription = getString(title)
            ViewCompat.setAccessibilityHeading(row.techniqueHeader, true)
            row.techniqueHeader.setOnClickListener {
                expanded = if (expanded == index) -1 else index
                renderExpansion()
            }
            rows += row
            binding.techniqueList.addView(row.root)
        }
        renderExpansion()
    }

    private fun renderExpansion() = rows.forEachIndexed { index, row ->
        val open = index == expanded
        row.techniqueDescription.visibility = if (open) View.VISIBLE else View.GONE
        row.techniqueChevron.rotation = if (open) 90f else 0f
        ViewCompat.setStateDescription(row.techniqueHeader, getString(if (open) R.string.breath_expanded else R.string.breath_collapsed))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("expandedTechnique", expanded)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        rows.clear()
        _binding = null
        super.onDestroyView()
    }
}
