package com.bayraktar.healthybackandneck.ui.ExerciseMovesEnd

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseMovesEndBinding
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentArgs
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.ExerciseMovesFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseMovesEndFragment : Fragment() {

    private var _binding: FragmentExerciseMovesEndBinding? = null
    val binding get() = _binding!!

    private var exerciseDayModel: ExerciseDay? = null
    private var exerciseList = ArrayList<ExerciseDayExercise>()
    private var exerciseArray: Array<ExerciseDayExercise>? = null
    private var subExerciseList = ArrayList<SubExerciseDayExercise>()
    private var subExerciseArray: Array<SubExerciseDayExercise>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseMovesEndBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.btnEnd.setOnClickListener {
            val action = ExerciseMovesEndFragmentDirections.actionExerciseMovesEndFragmentToIdHomepageFragment()
            view?.findNavController()?.navigate(action)
        }
    }

    private fun getSetData() = with(binding) {
        val args = ExerciseMovesEndFragmentArgs.fromBundle(requireArguments())

        exerciseArray = args.exerciseNewList
        subExerciseArray = args.subExerciseNewList
        exerciseDayModel = args.exerciseDayModel

        if (exerciseDayModel!!.exerciseCount > 1) {
            exerciseList = ArrayList(exerciseArray!!.asList())
            txttime.text = exerciseDayModel?.exerciseTime.toString()
            txtexercise.text = exerciseDayModel?.exerciseCount.toString()
            txtkcal.text = exerciseDayModel?.exerciseKcal.toString()

        } else {
            subExerciseList = ArrayList(subExerciseArray!!.asList())

            txttime.text = "07:30"
            txtexercise.text = "7"
            txtkcal.text = "300"
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