package com.bayraktar.healthybackandneck.ui.ExerciseMovesRest

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesRestBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDaySubAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesRestFragment : Fragment() {

    private var _binding: FragmentExerciseMovesRestBinding? = null
    val binding get() = _binding!!


    private var timeSelected = 20
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

        binding.txtTimeLeft.text = "20"
        binding.pbTimer.max = timeSelected

        getData()
        startTimerSetup()


        binding.skipRest.setOnClickListener {
            val action =
                ExerciseMovesRestFragmentDirections.actionExerciseMovesRestFragmentToExerciseMovesFragment2(
                    currentExerciseIndex + 1,
                    exerciseList.toTypedArray(),
                    exerciseDayModel!!
                )
            view.findNavController().navigate(action)

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
                albelRank.text = exerciseList.size.toString() +  getString(R.string.lbl_step)


        }
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
        val progressBar = binding.pbTimer
        progressBar.progress = timeProgress
        timecountDown = object : CountDownTimer(
            (timeSelected * 1000).toLong() - pauseOffSetL * 1000, 1000
        ) {
            override fun onTick(p0: Long) {
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
                            exerciseDayModel!!
                        )
                    view?.findNavController()?.navigate(action)

            }

        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (timecountDown != null) {
            timecountDown?.cancel()
            timeProgress = 0
        }
    }
}