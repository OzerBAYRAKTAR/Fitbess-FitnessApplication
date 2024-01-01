package com.bayraktar.healthybackandneck.ui.ExerciseMovesReady

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesReadyBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentArgs
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDaySubAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesReadyFragment : Fragment() {

    private var _binding: FragmentExerciseMovesReadyBinding? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesReadyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MobileAds.initialize(requireContext()) {}

        //bannder id => ca-app-pub-4754194669476617/5976831939

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        binding.txtTimeleft.text = "30"
        binding.pbTimer.max = timeSelected


        startTimerSetup()

        getSetData()

        binding.goNextbtn.setOnClickListener {
                val action =
                    ExerciseMovesReadyFragmentDirections.actionExerciseMovesFragmentToExerciseMovesFragment2(
                        0, exerciseList.toTypedArray(), exerciseDayModel
                    )
                view.findNavController().navigate(action)

        }
        binding.goMenu.setOnClickListener {
            backstack()
        }

    }

    private fun getSetData() = with(binding) {
        val args = ExerciseMovesReadyFragmentArgs.fromBundle(requireArguments())
        exerciseDayModel = args.exerciseDayModel

        exerciseArray = args.exerciseNewList

            exerciseList = ArrayList(exerciseArray!!.asList())


    }

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(ContentValues.TAG, "Back pressed")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
                playpause.setBackgroundResource((R.drawable.startexercise))
                startTimer(pauseOffSet)
                isStart = false
            } else {
                isStart = true
                playpause.setBackgroundResource((R.drawable.stopexercise))
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
                val timeLeft = binding.txtTimeleft
                timeLeft.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {
                    val exerciseArray: Array<SubExerciseDayExercise>? = null
                    val action =
                        ExerciseMovesReadyFragmentDirections.actionExerciseMovesFragmentToExerciseMovesFragment2(
                            1, exerciseList.toTypedArray(), exerciseDayModel
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