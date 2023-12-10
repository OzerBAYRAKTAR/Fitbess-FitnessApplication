package com.bayraktar.healthybackandneck.ui.ExerciseMoves

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesBinding
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesReadyBinding
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragmentArgs
import com.bayraktar.healthybackandneck.ui.ExerciseMovesReady.ExerciseMovesReadyFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesFragment : Fragment() {
    private var _binding: FragmentExerciseMovesBinding? = null
    val binding get() = _binding!!


    private var timeSelected = 30
    private var timecountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true

    private var exerciseDayModel= ExerciseDay(1,1,1,1,1,1)
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var subExerciseList = ArrayList<SubExerciseDayExercise>()
    private var subExerciseArray: Array<SubExerciseDayExercise>? = null

    //private var receivedExerciseDayModel : ExerciseDay? = null

    private var currentExerciseIndex = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTimeLeft.text = "10"
        binding.pbTimer.max = timeSelected

        backstack()
        getSetData()
        startTimerSetup()

        btnPreviousMoveClicked()
        btnNextMoveClicked()
        btnPauseClicked()
        binding.exerciseInfo.setOnClickListener {
            showCustomExerciseInfoDialog()
        }
        binding.quitExerciseToDetailDay.setOnClickListener {
            btnCloseClicked()
        }


    }

    private fun btnPauseClicked() = with(binding) {
        playpause.setOnClickListener {
            startTimerSetup()
            // Update the image here if necessary
            if (isStart) {
                playpause.setBackgroundResource(R.drawable.stopexercise)
            } else {
                playpause.setBackgroundResource(R.drawable.startexercise)
            }
        }
    }

    private fun btnCloseClicked() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_cancel, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        val positiveBtn: Button = dialogView.findViewById(R.id.dialogYes)
        val negativeBtn: Button = dialogView.findViewById(R.id.dialogNo)

        negativeBtn.setOnClickListener {
            dialog.dismiss()
        }
        positiveBtn.setOnClickListener {
            val action =
                ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToIdHomepageFragment()
            view?.findNavController()?.navigate(action)
            dialog.dismiss()
        }

    }

    private fun showCustomExerciseInfoDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.info_custom_layout, null)

        if (exerciseArray?.isNotEmpty() == true){
            val currentModel = exerciseList[currentExerciseIndex]

            val exerciseTitle: TextView = dialogView.findViewById(R.id.exerciseTitle)
            val exerciseDescription: TextView = dialogView.findViewById(R.id.exerciseDescription)

            exerciseTitle.text = getString(R.string.desc)
            exerciseDescription.text = currentModel.exerciseDescription
        } else {
            val currentModel = subExerciseList[currentExerciseIndex]

            val exerciseTitle: TextView = dialogView.findViewById(R.id.exerciseTitle)
            val exerciseDescription: TextView = dialogView.findViewById(R.id.exerciseDescription)

            exerciseTitle.text = getString(R.string.desc)
            exerciseDescription.text = currentModel.exerciseDescription
        }


        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
            .setPositiveButton(R.string.close) { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun getSetData() = with(binding) {
        val args = ExerciseMovesFragmentArgs.fromBundle(requireArguments())

        //receivedExerciseDayModel = args.exerciseDayModel
        if (args.exerciseDayModel != null) {
            exerciseDayModel = args.exerciseDayModel!!
        }
        exerciseArray = args.exerciseNewList

        subExerciseArray = args.subExerciseNewList
        currentExerciseIndex = args.exerciseIndex

        if (exerciseArray?.isNotEmpty() == true) {
            exerciseList = ArrayList(exerciseArray!!.asList())

            val currentModel = exerciseList[currentExerciseIndex]
            moveGif.setImageResource(currentModel.image)
            exerciceName.text = currentModel.exerciseName
            txtRank.text = (currentExerciseIndex + 1).toString()
            albelRank.text = "/${exerciseList.size.toString()}"
        } else {
            subExerciseList = ArrayList(subExerciseArray!!.asList())

            val currentModel = subExerciseList[currentExerciseIndex]
            moveGif.setImageResource(currentModel.image)
            exerciceName.text = currentModel.exerciseName
            txtRank.text = (currentExerciseIndex + 1).toString()
            albelRank.text = "/${subExerciseList.size.toString()}"
        }


    }

    private fun timePause() {

        if (timecountDown != null) {
            timecountDown?.cancel()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startTimerSetup() = with(binding) {

        val isTimerRunning = !isStart

        if (timeSelected > timeProgress) {
            if (isStart) {
                playpause.setImageResource((R.drawable.startexercise))
                startTimer(pauseOffSet)
                isStart = false
            } else {
                isStart = true
                playpause.setImageResource((R.drawable.stopexercise))
                timePause()
            }
            isStart = isTimerRunning
        }
    }


    private fun startTimer(pauseOffSetL: Long) {
        val args = ExerciseMovesFragmentArgs.fromBundle(requireArguments())

        val receivedExerciseDayModel: ExerciseDay? = args.exerciseDayModel
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
                if (currentExerciseIndex + 1 == exerciseList.size) {
                    // If the current exercise is the last one, navigate to the end fragment directly
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesEndFragment(
                            subExerciseList.toTypedArray(),
                            exerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                } else {
                    //if current exercise is not the last one, keep go rest fragment
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesRestFragment(
                            currentExerciseIndex,
                            exerciseList.toTypedArray(),
                            subExerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                }

            }

        }.start()
    }

    private fun btnNextMoveClicked() = with(binding) {
        goNext.setOnClickListener {
            if (exerciseList.size > 0) {
                if (currentExerciseIndex + 1 == exerciseList.size) {
                    // If the current exercise is the last one, navigate to the end fragment directly
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesEndFragment(
                            subExerciseList.toTypedArray(),
                            exerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                } else {
                    //if current exercise is not the last one, keep go rest fragment
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesRestFragment(
                            currentExerciseIndex,
                            exerciseList.toTypedArray(),
                            subExerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                }
            }else {
                if (currentExerciseIndex + 1 == subExerciseList.size) {
                    // If the current exercise is the last one, navigate to the end fragment directly
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesEndFragment(
                            subExerciseList.toTypedArray(),
                            exerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                } else {
                    //if current exercise is not the last one, keep go rest fragment
                    val action =
                        ExerciseMovesFragmentDirections.actionExerciseMovesFragment2ToExerciseMovesRestFragment(
                            currentExerciseIndex,
                            exerciseList.toTypedArray(),
                            subExerciseList.toTypedArray(),
                            exerciseDayModel
                        )
                    view?.findNavController()?.navigate(action)
                }
            }

        }
    }

    private fun restartTimer() {
        if (timecountDown != null) {
            timecountDown?.cancel()
            timecountDown = null
        }
        val progressBar = binding.pbTimer
        progressBar.progress = 0 // Reset progress to 0
        timeProgress = 0 // Reset time progress
        val remainingTime = timeSelected * 1000
        timecountDown = object : CountDownTimer(remainingTime.toLong(), 1000) {
            override fun onTick(p0: Long) {
                timeProgress++
                progressBar.progress = timeProgress
                val timeLeft = binding.txtTimeLeft
                timeLeft.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {
                println("Timer finished")
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun btnPreviousMoveClicked() = with(binding) {
        goBack.setOnClickListener {
            if (currentExerciseIndex > 0) {
                currentExerciseIndex -= 1

                if (exerciseList.size > 0) {
                    clearResource()
                    val currentModel = exerciseList[currentExerciseIndex]
                    moveGif.setImageResource(currentModel.image)
                    exerciceName.text = currentModel.exerciseName
                    txtRank.text = (currentExerciseIndex + 1).toString()
                    albelRank.text = "/${exerciseList.size.toString()}"

                    restartTimer()
                }else {
                    clearResource()
                    val currentModel = subExerciseList[currentExerciseIndex]
                    moveGif.setImageResource(currentModel.image)
                    exerciceName.text = currentModel.exerciseName
                    txtRank.text = (currentExerciseIndex + 1).toString()
                    albelRank.text = "/${subExerciseList.size.toString()}"

                    restartTimer()
                }
            }
        }
    }

    private fun clearResource() = with(binding) {
        moveGif.setImageResource(R.drawable.placehlder)
        exerciceName.text = ""
        txtRank.text = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timecountDown != null) {
            timecountDown?.cancel()
            timeProgress = 0
        }
    }
    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                btnCloseClicked()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}