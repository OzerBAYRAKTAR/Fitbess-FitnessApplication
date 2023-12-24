package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailThird

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDetailThirdBinding
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstAdapter
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstFragmentDirections
import com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst.ExerciseDetailFirstVievModel
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.nio.charset.Charset


@AndroidEntryPoint
class ExerciseDetailThirdFragment : Fragment(), RecyclerViewClickListener {

    private var _binding: FragmentExerciseDetailThirdBinding?= null
    val binding get() = _binding!!

    private val viewModel: ExerciseDetailThirdViewModel by viewModels()

    private var detailList = emptyList<ExerciseDay>()
    private val exerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private lateinit var thirdAdapter: ExerciseDetailAdapterThird
    private var selectedModel: ExerciseDay?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDetailThirdBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()

        addToDetailList()
        observeLevelThird()
        observeExerciseDay()
        backstack()
        binding.imageBack.setOnClickListener {
            val action = ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToIdHomepageFragment()
            view.findNavController().navigate(action)
        }


        viewModel.fetchExerciseDayExercisesWithLevelThird()


    }
    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    ExerciseDetailThirdFragmentDirections.actionExerciseDetailThirdFragmentToIdHomepageFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun addToDetailList() {
        detailList = listOf(
            ExerciseDay(
                day = 1,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 340,
                dayId = 1,
                homeId = 3

            ),
            ExerciseDay(
                day = 2,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 240,
                dayId = 2,
                homeId = 3
            ),
            ExerciseDay(
                day = 3,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 375,
                dayId = 3,
                homeId = 3
            ),
            ExerciseDay(
                day = 4,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 300,
                dayId = 4,
                homeId = 3
            ), ExerciseDay(
                day = 5,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 350,
                dayId = 5,
                homeId = 3
            ), ExerciseDay(
                day = 6,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 290,
                dayId = 6,
                homeId = 3
            ), ExerciseDay(
                day = 7,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 330,
                dayId = 7,
                homeId = 3
            ), ExerciseDay(
                day = 8,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 310,
                dayId = 8,
                homeId = 3
            ), ExerciseDay(
                day = 9,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 290,
                dayId = 9,
                homeId = 3
            ),
            ExerciseDay(
                day = 10,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 340,
                dayId = 10,
                homeId = 3
            ),
            ExerciseDay(
                day = 11,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 240,
                dayId = 11,
                homeId = 3
            ),
            ExerciseDay(
                day = 12,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 270,
                dayId = 12,
                homeId = 3
            ),
            ExerciseDay(
                day = 13,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 290,
                dayId = 13,
                homeId = 3
            ),
            ExerciseDay(
                day = 14,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 330,
                dayId = 14,
                homeId = 3
            ),
            ExerciseDay(
                day = 15,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 330,
                dayId = 15,
                homeId = 3
            ), ExerciseDay(
                day = 16,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 410,
                dayId = 16,
                homeId = 3
            ), ExerciseDay(
                day = 17,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 290,
                dayId = 17,
                homeId = 3
            ),
            ExerciseDay(
                day = 18,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 340,
                dayId = 18,
                homeId = 3
            ),
            ExerciseDay(
                day = 19,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 240,
                dayId = 19,
                homeId = 3
            ),
            ExerciseDay(
                day = 20,
                exerciseCount = 8,
                exerciseTime = 8,
                exerciseKcal = 270,
                dayId = 20,
                homeId = 3
            ),
            ExerciseDay(
                day = 21,
                exerciseCount = 8,
                exerciseTime = 7,
                exerciseKcal = 290,
                dayId = 21,
                homeId = 3
            ),
        )
        thirdAdapter.setData(detailList)
    }

    private fun observeLevelThird() {
        viewModel.exerciseDayExercisesLevelThird.observe(viewLifecycleOwner, Observer { exercises ->
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
                if (item.optInt("Level") == 3) {
                    val exercise = ExerciseDayExercise(
                        id = item.optInt("id"),
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
                        level = item.optInt("Level"),
                        titleName = "noTitle"
                    )
                    exerciseDayExercise.add(exercise)
                }
            }
            viewModel.insertExerciseDayExercise(exerciseDayExercise)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        thirdAdapter = ExerciseDetailAdapterThird(detailList,this@ExerciseDetailThirdFragment)
        listDaysOfWeek.adapter = thirdAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        selectedModel = detailList[position]
        val selectedDay = position + 1
        viewModel.getExerciseListWithDayID(selectedDay,3)
        observeListWithDayId()
    }

    private fun observeListWithDayId() {
        viewModel.getExerciseListWithDay.observe(viewLifecycleOwner, Observer { exercise ->
            if (exercise != null) {
                exerciseListSend.clear()
                exerciseListSend.addAll(exercise)
            }
            val exerciseLevel = getString(R.string.hard_level)
            val exerciseArray = exerciseListSend.toTypedArray()
            val action= ExerciseDetailThirdFragmentDirections.actionExerciseDetailThirdFragmentToDetailDayFragment(
                exerciseArray, selectedModel!!,exerciseLevel)
            view?.findNavController()?.navigate(action)
        })
    }


}