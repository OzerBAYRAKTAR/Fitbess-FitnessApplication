package com.bayraktar.healthybackandneck.ui.ExerciseDetailFirst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.Models.ExerciseDetailModel.ExerciseDetailModel
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDaysOfWeekBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class ExerciseDetailFirstFragment : Fragment(),RecyclerViewClickListener {

    private var _binding: FragmentExerciseDaysOfWeekBinding? = null
    val binding get() = _binding!!

    private var detailList = emptyList<ExerciseDetailModel>()
    private lateinit var firstAdapter: ExerciseDetailFirstAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDaysOfWeekBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()

        detailList = listOf(
            ExerciseDetailModel(
                percent = 40,
                day = 1,
                exerciseCount = 7,
                exerciseTime = 7.00,
                exerciseKcal = 340
            ),
            ExerciseDetailModel(
                percent = 30,
                day = 2,
                exerciseCount = 6,
                exerciseTime = 7.00,
                exerciseKcal = 440
            ),
            ExerciseDetailModel(
                percent = 50,
                day = 3,
                exerciseCount = 8,
                exerciseTime = 6.45,
                exerciseKcal = 375
            ),
            ExerciseDetailModel(
                percent = 45,
                day = 4,
                exerciseCount = 8,
                exerciseTime = 7.50,
                exerciseKcal = 300
            ), ExerciseDetailModel(
                percent = 35,
                day = 5,
                exerciseCount = 6,
                exerciseTime = 6.30,
                exerciseKcal = 350
            ), ExerciseDetailModel(
                percent = 21,
                day = 6,
                exerciseCount = 9,
                exerciseTime = 5.40,
                exerciseKcal = 290
            ), ExerciseDetailModel(
                percent = 38,
                day = 7,
                exerciseCount = 6,
                exerciseTime = 6.50,
                exerciseKcal = 330
            ), ExerciseDetailModel(
                percent = 81,
                day = 8,
                exerciseCount = 7,
                exerciseTime = 6.20,
                exerciseKcal = 410
            ), ExerciseDetailModel(
                percent = 43,
                day = 9,
                exerciseCount = 8,
                exerciseTime = 7.10,
                exerciseKcal = 190
            ),
            ExerciseDetailModel(
                percent = 63,
                day = 10,
                exerciseCount = 9,
                exerciseTime = 6.50,
                exerciseKcal = 340
            ),
            ExerciseDetailModel(
                percent = 17,
                day = 11,
                exerciseCount = 7,
                exerciseTime = 5.30,
                exerciseKcal = 240
            ),
            ExerciseDetailModel(
                percent = 5,
                day = 12,
                exerciseCount = 8,
                exerciseTime = 8.30,
                exerciseKcal = 270
            ),
            ExerciseDetailModel(
                percent = 91,
                day = 13,
                exerciseCount = 8,
                exerciseTime = 7.20,
                exerciseKcal = 290
            ),
            ExerciseDetailModel(
                percent = 59,
                day = 14,
                exerciseCount = 7,
                exerciseTime = 6.30,
                exerciseKcal = 430
            ),
        )
        firstAdapter.setData(detailList)
    }
    private fun setRecyclerview() = with(binding) {
        listDaysOfWeek.layoutManager = LinearLayoutManager(requireContext())
        firstAdapter = ExerciseDetailFirstAdapter(detailList,this@ExerciseDetailFirstFragment)
        listDaysOfWeek.adapter = firstAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        TODO("Not yet implemented")
    }


}