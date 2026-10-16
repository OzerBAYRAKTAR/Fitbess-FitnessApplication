package com.bayraktar.healthybackandneck.ui.ExerciseMovesReady

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.bayraktar.healthybackandneck.utils.MoveAccess
import com.bayraktar.healthybackandneck.utils.MoveAccessViewModel
import androidx.fragment.app.activityViewModels
import android.content.ContentValues
import com.bayraktar.healthybackandneck.utils.WorkoutNavigation
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
import com.bayraktar.healthybackandneck.utils.loadForLifecycle
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesReadyFragment : Fragment() {

    private val session: com.bayraktar.healthybackandneck.ui.ExerciseMoves.WorkoutSessionViewModel by activityViewModels()
    private val access: MoveAccessViewModel by viewModels()
    private lateinit var navigation: WorkoutNavigation
    private var resumeTimer = false
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
        timeProgress = 0
        pauseOffSet = 0
        isStart = true
        resumeTimer = false
        navigation = WorkoutNavigation(this, R.id.exerciseMovesFragment)

        //bannder id => ca-app-pub-4754194669476617/5976831939

        mAdView = binding.adView
        mAdView.visibility = View.GONE

        binding.txtTimeleft.text = "30"
        binding.pbTimer.max = timeSelected


        getSetData()
        if (exerciseList.any(MoveAccess::requiresReward)) {
            binding.goNextbtn.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                val earned = access.earned.first { it != null }.orEmpty()
                exerciseList = ArrayList(exerciseList.filter { MoveAccess.allowed(it, earned) })
                binding.goNextbtn.isEnabled = exerciseList.isNotEmpty()
                if (exerciseList.isNotEmpty()) {
                    if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) startTimerSetup()
                    else { resumeTimer = true; isStart = false }
                } else binding.txtTimeleft.setText(R.string.unlock_to_start)
            }
        } else startTimerSetup()

        binding.goNextbtn.setOnClickListener {
                if (exerciseList.isEmpty() || !binding.goNextbtn.isEnabled) return@setOnClickListener
                val action =
                    ExerciseMovesReadyFragmentDirections.actionExerciseMovesFragmentToExerciseMovesFragment2(
                        0, exerciseList.toTypedArray(), exerciseDayModel
                    )
                navigation.navigate(action) {
                    timePause()
                    session.begin(exerciseDayModel, exerciseList.size, exerciseList.firstOrNull()?.titleName.orEmpty())
                }

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
            ((timeSelected * 1000).toLong() - pauseOffSetL * 1000).coerceAtLeast(1), 1000
        ) {
            override fun onTick(p0: Long) {
                if (_binding == null) return
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
                            0, exerciseList.toTypedArray(), exerciseDayModel
                        )
                    navigation.navigate(action) {
                    timePause()
                    session.begin(exerciseDayModel, exerciseList.size, exerciseList.firstOrNull()?.titleName.orEmpty())
                }

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
        if (resumeTimer) {
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