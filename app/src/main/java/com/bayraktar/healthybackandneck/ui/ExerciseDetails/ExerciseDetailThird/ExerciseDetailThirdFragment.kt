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

    private var _binding: FragmentExerciseDetailThirdBinding? = null
    val binding get() = _binding!!

    private val viewModel: ExerciseDetailThirdViewModel by viewModels()

    private var detailList = ArrayList<ExerciseDay>()
    private val exerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private lateinit var thirdAdapter: ExerciseDetailAdapterThird
    private var selectedModel: ExerciseDay? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDetailThirdBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerview()

        observeLevelThird()
        observeDayListLevelThird()
        backstack()
        binding.imageBack.setOnClickListener {
            val action =
                ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToIdHomepageFragment()
            view.findNavController().navigate(action)
        }


        viewModel.fetchExerciseDayExercisesWithLevelThird()
        viewModel.fetchExerciseDayListWithLevelThird()


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


    private fun observeLevelThird() {
        viewModel.exerciseDayExercisesLevelThird.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExercises()
            } else {
                println("liste var")
            }
        })
    }

    private fun observeDayListLevelThird() {
        viewModel.exerciseDayLevelThird.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExerciseDayList()
            } else {
                thirdAdapter.setData(exercises)
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun addToRoomExerciseDayList() {
        try {
            val inputStream = context?.assets?.open("thirdday.json")
            val size = inputStream?.available() ?: 0
            val buffer = ByteArray(size)
            inputStream?.read(buffer)
            inputStream?.close()
            val jsonString = String(buffer, Charset.defaultCharset())

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val exercise = ExerciseDay(
                    dayId = item.optInt("dayId"),
                    level = item.optInt("level"),
                    isCompleted = item.optBoolean("isCompleted"),
                    exerciseCount = item.optInt("exerciseCount"),
                    day = item.optInt("day"),
                    exerciseTime = item.optInt("exerciseTime"),
                    exerciseKcal = item.optInt("exerciseKcal"),
                    homeId = item.optInt("homeId")
                )
                detailList.add(exercise)
            }
            viewModel.insertExerciseDaysList(detailList)
            thirdAdapter.setData(detailList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
        thirdAdapter = ExerciseDetailAdapterThird(detailList, this@ExerciseDetailThirdFragment)
        listDaysOfWeek.adapter = thirdAdapter
    }

    override fun recyclerviewListClicked(v: View, position: Int) {
        selectedModel = detailList[position]
        val selectedDay = position + 1
        viewModel.getExerciseListWithDayID(selectedDay, 3)
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
            val action =
                ExerciseDetailThirdFragmentDirections.actionExerciseDetailThirdFragmentToDetailDayFragment(
                    exerciseArray, selectedModel!!, exerciseLevel
                )
            view?.findNavController()?.navigate(action)
        })
    }


}