package com.bayraktar.healthybackandneck.ui.HomePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentBodyRegionBinding
import com.bayraktar.healthybackandneck.databinding.ItemRegionWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BodyRegionFragment : Fragment() {
    private var _binding: FragmentBodyRegionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrainingDiscoveryViewModel by viewModels()
    private var selectedKey = "back"
    private var openJob: Job? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        selectedKey = state?.getString("regionKey") ?: arguments?.getString("regionKey") ?: "back"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentBodyRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        if (resources.configuration.screenWidthDp < 360 || resources.configuration.fontScale > 1.2f) {
            binding.regionPicker.orientation = android.widget.LinearLayout.VERTICAL
            binding.bodyMap.layoutParams = android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (160 * resources.displayMetrics.density).toInt())
            binding.regionGrid.layoutParams = android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            binding.regionGrid.columnCount = 2
        }
        populateRegionGrid(binding.regionGrid, TrainingCatalog.regions.filter { it.key != "warmup" }) { selectRegion(it.key) }
        binding.warmupChip.visibility = View.GONE
        selectRegion(selectedKey)
    }

    private fun selectRegion(key: String) {
        selectedKey = key
        openJob?.cancel()
        binding.loading.visibility = View.GONE
        val region = TrainingCatalog.regions.firstOrNull { it.key == key } ?: TrainingCatalog.regions.first()
        binding.bodyMap.setImageResource(region.silhouette)
        binding.bodyMap.contentDescription = getString(region.title)
        binding.regionTitle.setText(region.title)
        binding.regionTitle.visibility = if (region.key == "warmup") View.GONE else View.VISIBLE
        binding.regionPicker.visibility = if (region.key == "warmup") View.GONE else View.VISIBLE
        binding.screenTitle.setText(if (region.key == "warmup") R.string.region_warmup else R.string.discovery_choose_region)
        for (index in 0 until binding.regionGrid.childCount) {
            binding.regionGrid.getChildAt(index).isSelected = binding.regionGrid.getChildAt(index).tag == region.key
        }
        binding.workoutList.removeAllViews()
        region.workouts.forEach { workout ->
            val row = ItemRegionWorkoutBinding.inflate(layoutInflater, binding.workoutList, false)
            row.workoutTitle.setText(workout.title)
            row.workoutImage.setImageResource(workout.image)
            row.workoutMeta.setText(R.string.discovery_view_moves)
            row.root.setOnClickListener { openWorkout(workout) }
            binding.workoutList.addView(row.root)
        }
    }

    private fun openWorkout(workout: RegionWorkout) {
        openJob?.cancel()
        val context = requireContext().applicationContext.createConfigurationContext(resources.configuration)
        openJob = viewLifecycleOwner.lifecycleScope.launch {
            binding.loading.visibility = View.VISIBLE
            try {
                val exercises = viewModel.exercises(workout)
                if (exercises.isEmpty()) {
                    Toast.makeText(context, R.string.discovery_load_failed, Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val controller = findNavController()
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                    !parentFragmentManager.isStateSaved && controller.currentDestination?.id == R.id.bodyRegionFragment) {
                    controller.navigate(BodyRegionFragmentDirections.actionBodyRegionFragmentToDetailDayFragment(
                        exercises.toTypedArray(), null, getString(workout.title)))
                }
            } catch (error: CancellationException) { throw error
            } catch (error: Exception) {
                Toast.makeText(context, R.string.discovery_load_failed, Toast.LENGTH_SHORT).show()
            } finally { _binding?.loading?.visibility = View.GONE }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("regionKey", selectedKey)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() { openJob?.cancel(); _binding = null; super.onDestroyView() }
}
