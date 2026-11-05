package com.bayraktar.healthybackandneck.ui.HomePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentProgramLevelBinding
import com.bayraktar.healthybackandneck.databinding.ItemProgramLevelCardBinding

/** The three program entrances share the existing day lists and saved progress. */
class ProgramLevelFragment : Fragment() {
    private var _binding: FragmentProgramLevelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentProgramLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        bindCard(binding.programBeginner, 1, R.string.discovery_level_beginner,
            R.string.level_hub_beginner_hint, R.drawable.program_level_beginner, R.id.exerciseDetailFirstFragment)
        bindCard(binding.programIntermediate, 2, R.string.discovery_level_intermediate,
            R.string.level_hub_intermediate_hint, R.drawable.program_level_intermediate, R.id.exerciseDetailSecondFragment)
        bindCard(binding.programAdvanced, 3, R.string.discovery_level_advanced,
            R.string.level_hub_advanced_hint, R.drawable.program_level_advanced, R.id.exerciseDetailThirdFragment)
    }

    private fun bindCard(card: ItemProgramLevelCardBinding, level: Int, title: Int, hint: Int, image: Int, destination: Int) {
        card.levelNumber.text = getString(R.string.level_hub_number, level)
        card.levelTitle.setText(title)
        card.levelDescription.setText(hint)
        card.levelImage.setImageResource(image)
        card.root.contentDescription = getString(R.string.level_hub_accessibility, getString(title), getString(hint))
        card.root.setOnClickListener {
            val controller = findNavController()
            if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                !parentFragmentManager.isStateSaved && controller.currentDestination?.id == R.id.programLevelFragment) {
                controller.navigate(destination)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
