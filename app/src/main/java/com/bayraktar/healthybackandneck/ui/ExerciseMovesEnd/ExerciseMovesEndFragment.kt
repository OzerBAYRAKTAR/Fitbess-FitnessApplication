package com.bayraktar.healthybackandneck.ui.ExerciseMovesEnd
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesEndBinding
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.WorkoutSessionViewModel
import com.bayraktar.healthybackandneck.utils.PreloadedInterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseMovesEndFragment : Fragment() {
    private var _binding: FragmentExerciseMovesEndBinding? = null
    private val binding get() = _binding!!
    private val session: WorkoutSessionViewModel by activityViewModels()
    private var transitionAd: PreloadedInterstitialAd? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentExerciseMovesEndBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        transitionAd = PreloadedInterstitialAd(requireActivity(), viewLifecycleOwner, getString(R.string.ad_interstitial_exercise_end))
        binding.btnEnd.setOnClickListener {
            val ad = transitionAd
            if (ad == null) returnToProgram() else ad.showOrContinue { returnToProgram() }
        }
        binding.retrySave.setOnClickListener { session.finish() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { returnToProgram() }
        })
        session.finish()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                session.saveState.collect { result ->
                    binding.retrySave.visibility = if (result is WorkoutSessionViewModel.SaveState.Failed) View.VISIBLE else View.GONE
                    when (result) {
                        is WorkoutSessionViewModel.SaveState.Saved -> {
                            val saved = result.session
                            binding.endTitle.setText(if (saved.isComplete) R.string.workout_complete_title else R.string.workout_partial_title)
                            binding.endSummary.text = getString(R.string.session_details, saved.activeMillis / 60000, (saved.activeMillis / 1000) % 60, saved.completedMoves, saved.plannedMoves, saved.skippedMoves)
                            binding.saveStatus.setText(R.string.workout_saved)
                        }
                        WorkoutSessionViewModel.SaveState.Failed -> binding.saveStatus.setText(R.string.workout_save_failed)
                        WorkoutSessionViewModel.SaveState.Saving -> binding.saveStatus.setText(R.string.workout_saving)
                        WorkoutSessionViewModel.SaveState.Idle -> binding.saveStatus.setText(R.string.workout_no_session)
                    }
                }
            }
        }
    }
    private fun goHome() {
        if (isAdded && !parentFragmentManager.isStateSaved && findNavController().currentDestination?.id == R.id.exerciseMovesEndFragment)
            findNavController().navigate(ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToIdHomepageFragment())
    }

    private fun returnToProgram() {
        if (!isAdded || parentFragmentManager.isStateSaved) return
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.exerciseMovesEndFragment) return
        val day = ExerciseMovesEndFragmentArgs.fromBundle(requireArguments()).exerciseDayModel
        if (day == null) {
            goHome()
            return
        }
        when (day.level) {
            1 -> navController.navigate(
                ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToExerciseDetailFirstFragment()
                    .setFocusDay(day.day)
            )
            2 -> navController.navigate(
                ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToExerciseDetailSecondFragment()
                    .setFocusDay(day.day)
            )
            3 -> navController.navigate(
                ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToExerciseDetailThirdFragment()
                    .setFocusDay(day.day)
            )
            else -> goHome()
        }
    }
    override fun onDestroyView() { transitionAd = null; _binding = null; super.onDestroyView() }
}
