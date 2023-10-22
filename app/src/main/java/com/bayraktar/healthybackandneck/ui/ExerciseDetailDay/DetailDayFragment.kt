package com.bayraktar.healthybackandneck.ui.ExerciseDetailDay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.databinding.FragmentDetailDayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDayFragment : Fragment() {

    private var _binding: FragmentDetailDayBinding? = null
    val binding get() = _binding!!

    private var exerciseList : ExerciseDay?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSetData()


        binding.startExercise.setOnClickListener {
            val action = DetailDayFragmentDirections.actionDetailDayFragmentToExerciseMovesFragment()
            view.findNavController().navigate(action)
        }
    }

    private fun getSetData() = with(binding) {
        //exerciseList = DetailDayFragmentArgs.fromBundle(requireArguments()).exerciseList

        txttime.text = exerciseList?.exerciseTime.toString()
        txtexercise.text = exerciseList?.exerciseCount.toString()
        txtkcal.text = exerciseList?.exerciseKcal.toString()
        txtdaysecond.text = exerciseList?.day.toString()

    }


}