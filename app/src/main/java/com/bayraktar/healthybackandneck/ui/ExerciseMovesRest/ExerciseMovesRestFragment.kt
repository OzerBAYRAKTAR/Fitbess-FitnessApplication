package com.bayraktar.healthybackandneck.ui.ExerciseMovesRest

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesRestBinding
import com.bayraktar.healthybackandneck.utils.WorkoutNavigation
import com.bayraktar.healthybackandneck.utils.workoutClock
import com.bayraktar.healthybackandneck.utils.workoutSessionTitle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesRestFragment : Fragment() {
 private var _binding: FragmentExerciseMovesRestBinding? = null
 val binding get() = _binding!!
 private lateinit var args: ExerciseMovesRestFragmentArgs
 private lateinit var navigation: WorkoutNavigation
 private var remaining = 30_000L
 private var deadline = 0L
 private var timer: CountDownTimer? = null
 private var dialogOpen = false
 override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
  _binding = FragmentExerciseMovesRestBinding.inflate(inflater, container, false)
  return binding.root
 }
 override fun onViewCreated(view: View, state: Bundle?) {
  args = ExerciseMovesRestFragmentArgs.fromBundle(requireArguments())
  remaining = state?.getLong("remaining") ?: 30_000L
  navigation = WorkoutNavigation(this, R.id.exerciseMovesRestFragment)
  val moves = args.exerciseNewList.orEmpty()
  val next = moves.getOrNull(args.exerciseIndex + 1) ?: return
  binding.gifImageView2.setImageResource(next.image)
  binding.exerciceName.text = next.exerciseName
  binding.txtRank.text = (args.exerciseIndex + 2).toString()
  binding.albelRank.text = "/${moves.size}"
  binding.sessionTitle.text = workoutSessionTitle(requireContext(), args.exerciseDayModel)
  binding.sessionProgress.max = moves.size
  binding.sessionProgress.progress = args.exerciseIndex + 1
  binding.pbTimer.max = 30
  binding.favoritebtn.visibility = View.GONE
  binding.adView.visibility = View.GONE
  binding.skipRest.setOnClickListener { advance() }
  binding.exitRest.setOnClickListener { confirmExit() }
  requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
   override fun handleOnBackPressed() { confirmExit() }
  })
  render()
 }
 private fun render() {
  binding.txtTimeLeft.text = workoutClock(remaining)
  binding.pbTimer.progress = ((remaining + 999) / 1000).toInt()
 }
 private fun startClock() {
  if (timer != null || dialogOpen || !isResumed) return
  deadline = SystemClock.elapsedRealtime() + remaining
  timer = object: CountDownTimer(remaining.coerceAtLeast(1), 200) {
   override fun onTick(left: Long) { remaining = left; if (_binding != null) render() }
   override fun onFinish() { remaining = 0; advance() }
  }.start()
 }
 private fun stopClock() {
  if (timer != null) remaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0)
  timer?.cancel(); timer = null
 }
 private fun advance() {
  navigation.navigate(ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesFragment2(
   args.exerciseIndex + 1, args.exerciseNewList!!, args.exerciseDayModel)) { stopClock() }
 }
 private fun confirmExit() {
  if (dialogOpen) return
  dialogOpen = true; stopClock()
  var leaving = false
  AlertDialog.Builder(requireContext()).setTitle(R.string.exit_workout_title).setMessage(R.string.exit_workout_message)
   .setNegativeButton(R.string.resume_workout, null)
   .setPositiveButton(R.string.exit_workout) { _, _ ->
    leaving = true
    navigation.navigate(ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesEndFragment(
     args.exerciseNewList!!, args.exerciseDayModel)) { stopClock() }
   }.setOnDismissListener { dialogOpen = false; if (!leaving && isResumed) startClock() }.show()
 }
 override fun onResume() { super.onResume(); if (::args.isInitialized) startClock() }
 override fun onPause() { stopClock(); super.onPause() }
 override fun onSaveInstanceState(outState: Bundle) {
  super.onSaveInstanceState(outState)
  outState.putLong("remaining", if(timer == null) remaining else (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0))
 }
 override fun onDestroyView() { stopClock(); _binding = null; super.onDestroyView() }
}
