package com.bayraktar.healthybackandneck.ui.ExerciseMovesRest

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.bayraktar.healthybackandneck.utils.WorkoutNavigation
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.utils.loadForLifecycle
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesRestBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDaySubAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragmentDirections
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesRestFragment : Fragment() {

    private lateinit var navigation: WorkoutNavigation
    private var resumeTimer = false
    private var exitDialogOpen = false
    private var _binding: FragmentExerciseMovesRestBinding? = null
    val binding get() = _binding!!

    private lateinit var mAdView: AdView
    private var timeSelected = 30
    private var timecountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true

    private var subExerciseList = ArrayList<SubExerciseDayExercise>()
    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var subExerciseArray: Array<SubExerciseDayExercise>? = null
    private var currentExerciseIndex = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesRestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeProgress = 0
        pauseOffSet = 0
        isStart = true
        resumeTimer = false
        navigation = WorkoutNavigation(this, R.id.exerciseMovesRestFragment)

        mAdView = binding.adView
        mAdView.visibility = View.GONE
        binding.txtTimeLeft.text = "30"
        binding.pbTimer.max = timeSelected

        binding.favoritebtn.visibility = View.GONE
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { confirmExit() }
        })
        getData()
        startTimerSetup()


        binding.skipRest.setOnClickListener {
            val action =
                ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesFragment2(
                    currentExerciseIndex + 1,
                    exerciseList.toTypedArray(),
                    exerciseDayModel
                )
            navigation.navigate(action) { timePause() }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData() = with(binding) {
        arguments?.let {
            currentExerciseIndex = ExerciseMovesRestFragmentArgs.fromBundle(it).exerciseIndex
            exerciseDayModel = ExerciseMovesRestFragmentArgs.fromBundle(it).exerciseDayModel
            exerciseArray = ExerciseMovesRestFragmentArgs.fromBundle(it).exerciseNewList

            exerciseList = ArrayList(exerciseArray!!.asList())

            val currentModel = exerciseList[currentExerciseIndex + 1]
            gifImageView2.setImageResource(currentModel.image)
            exerciceName.text = currentModel.exerciseName
            txtRank.text = (currentExerciseIndex + 2).toString()
            albelRank.text = "/" + exerciseList.size.toString() + getString(R.string.lbl_step)


        }
    }

    private fun confirmExit() {
        if (exitDialogOpen) return
        exitDialogOpen = true
        timePause()
        var leaving = false
        AlertDialog.Builder(requireContext()).setTitle(R.string.exit_workout_title)
            .setMessage(R.string.exit_workout_message)
            .setNegativeButton(R.string.resume_workout, null)
            .setPositiveButton(R.string.exit_workout) { _, _ ->
                leaving = true
                navigation.navigate(ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesEndFragment(
                    exerciseList.toTypedArray(), exerciseDayModel)) { timePause() }
            }
            .setOnDismissListener {
                exitDialogOpen = false
                if (!leaving && isResumed) startTimer(pauseOffSet)
            }
            .show()
    }

    private fun timePause() {

        if (timecountDown != null) {
            timecountDown?.cancel()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startTimerSetup() = with(binding) {
        if (timeSelected > timeProgress) {
            if (isStart) {
                startTimer(pauseOffSet)
                isStart = false
            } else {
                isStart = true
                timePause()
            }
        }
    }


    private fun startTimer(pauseOffSetL: Long) {
        timePause()
        val progressBar = binding.pbTimer
        progressBar.progress = timeProgress
        timecountDown = object : CountDownTimer(
            ((timeSelected * 1000).toLong() - pauseOffSetL * 1000).coerceAtLeast(1), 1000
        ) {
            override fun onTick(p0: Long) {
                if (_binding == null) return
                timeProgress++
                pauseOffSet = timeSelected.toLong() - p0 / 1000
                progressBar.progress = timeSelected - timeProgress
                val timeLeft = binding.txtTimeLeft
                timeLeft.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {

                val exerciseArray: Array<SubExerciseDayExercise>? = null
                val action =
                    ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesFragment2(
                        currentExerciseIndex + 1,
                        exerciseList.toTypedArray(),
                        exerciseDayModel
                    )
                navigation.navigate(action) { timePause() }

            }

        }.start()
    }


    override fun onPause() {
        resumeTimer = !isStart
        timePause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (resumeTimer && !exitDialogOpen) {
            resumeTimer = false
            startTimer(pauseOffSet)
        }
    }

    override fun onDestroyView() {
        timePause()
        timecountDown = null
        _binding = null
        super.onDestroyView()
    }
}