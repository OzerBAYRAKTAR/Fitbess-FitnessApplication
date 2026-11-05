package com.bayraktar.healthybackandneck.ui.HomePage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentHomePageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        binding.heroAction.setOnClickListener { navigate(R.id.programLevelFragment) }
        binding.allRegions.setOnClickListener { openRegion("back") }
        populateRegionGrid(binding.regionGrid, TrainingCatalog.regions.filter { it.key != "warmup" }) { openRegion(it.key) }
        binding.warmupAction.setOnClickListener { openRegion("warmup") }
        binding.breathAction.setOnClickListener { navigate(R.id.breathFragment) }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext()).setTitle(R.string.attention).setMessage(R.string.alertmessage)
                    .setPositiveButton(R.string.yes) { _, _ -> requireActivity().finishAffinity() }
                    .setNegativeButton(R.string.no, null).show()
            }
        })
    }

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
