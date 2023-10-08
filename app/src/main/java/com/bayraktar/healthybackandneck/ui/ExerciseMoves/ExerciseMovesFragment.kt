package com.bayraktar.healthybackandneck.ui.ExerciseMoves

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesBinding
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesReadyBinding
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragmentDirections

class ExerciseMovesFragment : Fragment() {
    private var _binding: FragmentExerciseMovesBinding?= null
    val binding get() = _binding!!


    private var timeSelected = 20
    private var timecountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTimeLeft.text = "20"
        binding.pbTimer.max = timeSelected


        startTimerSetup()

        binding.playpause.setOnClickListener {
            startTimerSetup()
        }

    }

    private fun timePause() {

        if (timecountDown != null) {
            timecountDown?.cancel()
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startTimerSetup()= with(binding) {
        if (timeSelected > timeProgress) {
            if (isStart) {
                playpause.setBackgroundResource((R.drawable.startexercise))
                startTimer(pauseOffSet)
                isStart=false
            }else{
                isStart = true
                playpause.setBackgroundResource((R.drawable.stopexercise))
                timePause()
            }
        }
    }

    private fun startTimer(pauseOffSetL: Long) {
        val progressBar =binding.pbTimer
        progressBar.progress = timeProgress
        timecountDown = object : CountDownTimer(
            (timeSelected*1000).toLong()-pauseOffSetL*1000,1000
        ) {
            override fun onTick(p0: Long) {
                timeProgress++
                pauseOffSet = timeSelected.toLong()-p0/1000
                progressBar.progress = timeSelected - timeProgress
                val timeLeft = binding.txtTimeLeft
                timeLeft.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {
                val action = ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesRestFragment()
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