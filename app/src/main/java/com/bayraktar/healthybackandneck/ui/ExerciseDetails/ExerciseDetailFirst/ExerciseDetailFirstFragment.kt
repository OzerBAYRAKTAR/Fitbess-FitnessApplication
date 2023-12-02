package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDaysOfWeekBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.nio.charset.Charset


@AndroidEntryPoint
class ExerciseDetailFirstFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentExerciseDaysOfWeekBinding? = null
    val binding get() = _binding!!
    private val viewModel: ExerciseDetailFirstVievModel by viewModels()

    private var detailList = emptyList<ExerciseDay>()
    private val exerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private lateinit var firstAdapter: ExerciseDetailFirstAdapter
    private var selectedModel: ExerciseDay?= null


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

        addToDetailList()
        observeLevelOne()
        observeExerciseDay()

        viewModel.fetchExerciseDayExercisesWithLevelOne()


    }

    private fun observeLevelOne() {
        viewModel.exerciseDayExercisesLevelOne.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExercises()
            } else {
                println("liste var")
            }
        })
    }

    private fun observeExerciseDay() {
        viewModel.exerciseDays.observe(viewLifecycleOwner, Observer { day ->
            if (day == null) {
                addToRoom()
            } else {
                println("day var")
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun addToRoomExercises() {
        try {
            val inputStream = context?.assets?.open("exercises.json")
            val size = inputStream?.available() ?: 0
            val buffer = ByteArray(size)
            inputStream?.read(buffer)
            inputStream?.close()
            val jsonString = String(buffer, Charset.defaultCharset())

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val exerciseDescriptionId = resources.getIdentifier(
                    item.optString("ExerciseDescription"), "string", requireContext().packageName
                )
                val exerciseDescription = if (exerciseDescriptionId != 0) {
                    getString(exerciseDescriptionId)
                } else {
                    getString(R.string.not_found)
                }
                if (item.optInt("Level") == 1) {
                    val exercise = ExerciseDayExercise(
                        dayId = item.optInt("DayId"),
                        step = item.optInt("Step"),
                        exerciseName = item.optString("ExerciseName"),
                        exerciseDescription = exerciseDescription,
                        image = resources.getIdentifier(
                            item.optString("Image"),
                            "drawable",
                            requireContext().packageName
                        ),
                        isExerciseCompleted = item.optBoolean("IsExerciseCompleted"),
                        exerciseId = item.optInt("ExerciseId"),
                        level = item.optInt("Level")
                    )
                    exerciseDayExercise.add(exercise)
                }
            }
            viewModel.insertExerciseDayExercise(exerciseDayExercise)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addToDetailList() {
        detailList = listOf(
            ExerciseDay(
                dayId = 1,
                day = 1,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 340,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 2,
                day = 2,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 320,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 3,
                day = 3,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 315,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 4,
                day = 4,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 330,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 5,
                day = 5,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 290,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 6,
                day = 6,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 305,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 7,
                day = 7,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 330,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 8,
                day = 8,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 280,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 9,
                day = 9,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 290,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 10,
                day = 10,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 240,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 11,
                day = 11,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 340,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 12,
                day = 12,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 270,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 13,
                day = 13,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 290,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 14,
                day = 14,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 330,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 15,
                day = 15,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 285,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 16,
                day = 16,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 325,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 17,
                day = 17,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 310,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 18,
                day = 18,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 330,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 19,
                day = 19,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 290,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 20,
                day = 20,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 285,
                homeId = 1
            ),
            ExerciseDay(
                dayId = 21,
                day = 21,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 335,
                homeId = 1
            ),
        )
        firstAdapter.setData(detailList)
    }

    private fun addToRoom() {
        lifecycleScope.launch {
            for (exerciseDay in detailList) {
                viewModel.insertExerciseDay(exerciseDay)
            }
        }
    }

    private fun setRecyclerview() = with(binding) {
        listDaysOfWeek.layoutManager = LinearLayoutManager(requireContext())
        firstAdapter = ExerciseDetailFirstAdapter(detailList, this@ExerciseDetailFirstFragment)
        listDaysOfWeek.adapter = firstAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        selectedModel = detailList[position]
        val selectedDay = position + 1
        viewModel.getExerciseListWithDayID(selectedDay,1)
        observeListWithDayId()
    }

    private fun observeListWithDayId() {
        viewModel.getExerciseListWithDay.observe(viewLifecycleOwner, Observer { exercise ->
            if (exercise != null) {
                exerciseListSend.clear()
                exerciseListSend.addAll(exercise)
            }
            val exerciseLevel = getString(R.string.easy_level_fil)
            val exerciseArray = exerciseListSend.toTypedArray()
            val action= ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToDetailDayFragment(
                    exerciseArray, selectedModel!!,exerciseLevel)
            view?.findNavController()?.navigate(action)
        })
    }

}