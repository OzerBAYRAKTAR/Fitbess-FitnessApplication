package com.bayraktar.healthybackandneck.ui.HomePage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrainingDiscoveryViewModel by viewModels()
    private var resumeTarget: ProgramResumeTarget? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.profileButton.setOnClickListener { navigate(R.id.id_profile_fragment) }
        binding.heroAction.setOnClickListener { openProgram(resumeTarget ?: ProgramResumeTarget(1, 1)) }
        binding.programBeginner.setOnClickListener { openProgram(ProgramResumeTarget(1, 1)) }
        binding.programIntermediate.setOnClickListener { openProgram(ProgramResumeTarget(2, 1)) }
        binding.programAdvanced.setOnClickListener { openProgram(ProgramResumeTarget(3, 1)) }
        binding.allRegions.setOnClickListener { openRegion("back") }
        populateRegionGrid(binding.regionGrid, TrainingCatalog.regions.filter { it.key != "warmup" }) { openRegion(it.key) }
        binding.warmupAction.setOnClickListener { openRegion("warmup") }
        binding.breathAction.setOnClickListener { navigate(R.id.breathFragment) }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sessions.collect { sessions ->
                    resumeTarget = programResumeTarget(sessions)
                    val target = resumeTarget
                    binding.heroContext.text = if (target == null) getString(R.string.discovery_new_program)
                    else getString(R.string.discovery_resume, levelName(target.level), target.day)
                    binding.heroAction.setText(if (target == null) R.string.start_now else R.string.discovery_continue)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext()).setTitle(R.string.attention).setMessage(R.string.alertmessage)
                    .setPositiveButton(R.string.yes) { _, _ -> requireActivity().finishAffinity() }
                    .setNegativeButton(R.string.no, null).show()
            }
        })
    }

    private fun levelName(level: Int) = getString(when (level) {
        2 -> R.string.hometitle2; 3 -> R.string.hometitle3; else -> R.string.hometitle1
    }).trim()

    private fun openProgram(target: ProgramResumeTarget) = navigate(when (target.level) {
        2 -> R.id.exerciseDetailSecondFragment
        3 -> R.id.exerciseDetailThirdFragment
        else -> R.id.exerciseDetailFirstFragment
    }, bundleOf("focusDay" to target.day))

    private fun openRegion(key: String) = navigate(R.id.bodyRegionFragment, bundleOf("regionKey" to key))

    private fun navigate(destination: Int, args: Bundle? = null) {
        val controller = findNavController()
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
            !parentFragmentManager.isStateSaved && controller.currentDestination?.id == R.id.id_homepage_fragment) {
            controller.navigate(destination, args)
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
