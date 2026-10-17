package com.bayraktar.healthybackandneck.ui.ExerciseMoves

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesBinding
import com.bayraktar.healthybackandneck.utils.WorkoutNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesFragment : Fragment() {
    private var _binding: FragmentExerciseMovesBinding? = null
    val binding get() = _binding!!
    private val viewModel: ExerciseMovesViewModel by viewModels()
    private val session: WorkoutSessionViewModel by activityViewModels()
    private lateinit var navigation: WorkoutNavigation
    private lateinit var args: ExerciseMovesFragmentArgs
    private var index = 0
    private var remaining = 30_000L
    private var deadline = 0L
    private var paused = false
    private var timecountDown: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentExerciseMovesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, state: Bundle?) {
        args = ExerciseMovesFragmentArgs.fromBundle(requireArguments())
        index = state?.getInt("index") ?: args.exerciseIndex
        remaining = state?.getLong("remaining") ?: 30_000L
        paused = state?.getBoolean("paused") ?: false
        navigation = WorkoutNavigation(this, R.id.exerciseMovesFragment2)
        binding.adView.visibility = View.GONE
        render()
        binding.goNext.setOnClickListener { advance(false) }
        binding.goBack.setOnClickListener {
            if (index > 0) {
                stopClock(); index--; remaining = 30_000L; render()
                if (!paused) startClock()
            }
        }
        binding.playpause.setOnClickListener {
            if (paused) { paused = false; startClock() } else { paused = true; stopClock() }
            renderTimer()
        }
        binding.exerciseInfo.setOnClickListener {
            val wasPaused = paused
            paused = true; stopClock()
            AlertDialog.Builder(requireContext()).setTitle(args.exerciseNewList!![index].exerciseName)
                .setMessage(args.exerciseNewList!![index].exerciseDescription)
                .setPositiveButton(R.string.close, null)
                .setOnDismissListener { paused = wasPaused; if (!paused && isResumed) startClock() }
                .show()
        }
        binding.favoritebtn.setOnClickListener {
            val move = args.exerciseNewList!![index]
            if (move.isFavourite) viewModel.updateIsFavouriteToFalse(move.exerciseId) else viewModel.updateExerciseById(move.exerciseId)
            move.isFavourite = !move.isFavourite
            binding.favoritebtn.setImageResource(if (move.isFavourite) R.drawable.heartfill else R.drawable.heartempty)
            binding.favoritebtn.contentDescription = getString(if (move.isFavourite) R.string.remove_favorite else R.string.add_favorite)
        }
        binding.quitExerciseToDetailDay.setOnClickListener { confirmExit() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { confirmExit() }
        })
    }
    private fun render() {
        val moves = args.exerciseNewList ?: return
        if (moves.isEmpty()) return
        index = index.coerceIn(moves.indices)
        binding.moveGif.setImageResource(moves[index].image)
        binding.exerciceName.text = moves[index].exerciseName
        binding.txtRank.text = (index + 1).toString()
        binding.albelRank.text = "/${moves.size}"
        binding.goBack.isEnabled = index > 0
        binding.pbTimer.max = 30
        binding.favoritebtn.setImageResource(if (moves[index].isFavourite) R.drawable.heartfill else R.drawable.heartempty)
        binding.favoritebtn.contentDescription = getString(if (moves[index].isFavourite) R.string.remove_favorite else R.string.add_favorite)
        renderTimer()
    }
    private fun renderTimer() {
        binding.txtTimeLeft.text = ((remaining + 999) / 1000).toString()
        binding.pbTimer.progress = ((remaining + 999) / 1000).toInt()
        binding.playpause.setImageResource(if (paused) R.drawable.fit_play else R.drawable.fit_pause)
        binding.playpause.contentDescription = getString(if (paused) R.string.resume_workout else R.string.pause_workout)
    }
    private fun startClock() {
        if (timecountDown != null || paused || !isResumed) return
        session.resume()
        deadline = SystemClock.elapsedRealtime() + remaining
        timecountDown = object : CountDownTimer(remaining.coerceAtLeast(1), 200) {
            override fun onTick(left: Long) { remaining = left; if (_binding != null) renderTimer() }
            override fun onFinish() { remaining = 0; advance(true) }
        }.start()
    }
    private fun stopClock() {
        if (timecountDown != null) remaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0)
        timecountDown?.cancel(); timecountDown = null
        session.pause()
    }
    private fun advance(completed: Boolean) {
        val moves = args.exerciseNewList ?: return
        val action = if (index == moves.lastIndex)
            ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesEndFragment(moves, args.exerciseDayModel)
        else ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesRestFragment(index, moves, args.exerciseDayModel)
        navigation.navigate(action) { stopClock(); if (completed) session.completeMove(index) }
    }
    private fun confirmExit() {
        val wasPaused = paused
        paused = true; stopClock()
        var leaving = false
        AlertDialog.Builder(requireContext()).setTitle(R.string.exit_workout_title).setMessage(R.string.exit_workout_message)
            .setNegativeButton(R.string.resume_workout, null)
            .setPositiveButton(R.string.exit_workout) { _, _ ->
                leaving = true
                navigation.navigate(ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesEndFragment(args.exerciseNewList!!, args.exerciseDayModel)) { stopClock() }
            }
            .setOnDismissListener { if (!leaving) { paused = wasPaused; if (!paused && isResumed) startClock() } }
            .show()
    }
    override fun onResume() { super.onResume(); if (!paused) startClock() }
    override fun onPause() { stopClock(); super.onPause() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("index", index)
        outState.putLong("remaining", if (timecountDown == null) remaining else (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0))
        outState.putBoolean("paused", paused)
    }
    override fun onDestroyView() { stopClock(); _binding = null; super.onDestroyView() }
}
