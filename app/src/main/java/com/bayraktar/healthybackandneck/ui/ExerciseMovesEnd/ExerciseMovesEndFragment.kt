package com.bayraktar.healthybackandneck.ui.ExerciseMovesEnd

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.utils.PreloadedInterstitialAd
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesEndBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetailDay.DetailDayFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentArgs
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.google.android.gms.ads.interstitial.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesEndFragment : Fragment() {

    private var _binding: FragmentExerciseMovesEndBinding? = null
    val binding get() = _binding!!
    private val viewModel: ExerciseMovesEndViewModel by viewModels()


    private var isAdLoaded = false

    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var transitionAd: PreloadedInterstitialAd? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesEndBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //interstealler canlı id => ca-app-pub-4754194669476617/6013325105
        //interstealler test id => ca-app-pub-3940256099942544/1033173712


        backstack()
        getSetData()
        btnEndClicked()


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

    private fun btnEndClicked() {
        transitionAd = PreloadedInterstitialAd(
            requireActivity(), viewLifecycleOwner, "ca-app-pub-1896269552628731/6394520905"
        )
        binding.prgsEnd.visibility = View.GONE
        binding.btnEnd.setOnClickListener {
            transitionAd?.showOrContinue {
                if (!isAdded || parentFragmentManager.isStateSaved) return@showOrContinue
                val controller = view?.findNavController() ?: return@showOrContinue
                if (controller.currentDestination?.id == R.id.exerciseMovesEndFragment) {
                    controller.navigate(ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToIdHomepageFragment())
                }
            }
        }
    }

    private fun getSetData() = with(binding) {
        val args = ExerciseMovesEndFragmentArgs.fromBundle(requireArguments())

        viewModel.updatecount()

        exerciseArray = args.exerciseNewList
        exerciseDayModel = args.exerciseDayModel

        if (exerciseDayModel!!.exerciseCount > 1) {
            exerciseList = ArrayList(exerciseArray!!.asList())
            txttime.text = exerciseDayModel?.exerciseTime.toString()
            txtexercise.text = exerciseDayModel?.exerciseCount.toString()
            txtkcal.text = exerciseDayModel?.exerciseKcal.toString()


            viewModel.updateIsCompletedToTrue(exerciseDayModel!!.level,exerciseDayModel!!.day + 1)

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

    override fun onDestroyView() {
        transitionAd = null
        _binding = null
        super.onDestroyView()
    }
}