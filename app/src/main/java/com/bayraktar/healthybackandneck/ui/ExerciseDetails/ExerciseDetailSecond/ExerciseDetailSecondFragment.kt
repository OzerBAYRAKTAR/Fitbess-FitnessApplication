package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailSecond

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDetailSecondBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.ExerciseItemClickListener
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerClicked
import com.bayraktar.healthybackandneck.utils.RewardedAds
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import java.nio.charset.Charset

@AndroidEntryPoint
class ExerciseDetailSecondFragment : Fragment(), ExerciseItemClickListener {

    private var _binding: FragmentExerciseDetailSecondBinding? = null
    val binding get() = _binding!!

    private var detailList = ArrayList<ExerciseDay>()
    private lateinit var secondAdapter: ExerciseDetailSecondAdapter

    private val handler = Handler()
    private val viewModel: ExerciseDetailSecondViewModel by viewModels()

    private val exerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private var selectedModel: ExerciseDay? = null
    private var myRewardedAds: RewardedAds? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseDetailSecondBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ca-app-pub-3940256099942544/5224354917 test
        MobileAds.initialize(requireContext()) {}
        binding.prgressStart.visibility = View.VISIBLE
        handler.postDelayed({
            binding.prgressStart.visibility = View.GONE
        }, 300)

        myRewardedAds = RewardedAds(requireActivity())
        myRewardedAds?.loadRewardedAds(R.string.rewarded_ad2)
        setRecyclerview()
        observeLevelTwo()
        observeDayListLevelOne()

        backstack()

        binding.imageBack.setOnClickListener {
            val action =
                ExerciseDetailSecondFragmentDirections.actionExerciseDetailSecondFragmentToIdHomepageFragment()
            view.findNavController().navigate(action)
        }

        viewModel.fetchExerciseDayExercisesWithLevelTwo()

        viewModel.fetchExerciseDayListWithLevelTwo()
    }

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    ExerciseDetailSecondFragmentDirections.actionExerciseDetailSecondFragmentToIdHomepageFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun observeLevelTwo() {
        viewModel.exerciseDayExercisesLevelTwo.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExercises()
            } else {
                println("liste var")
            }
        })
    }

    private fun observeDayListLevelOne() {
        viewModel.exerciseDayLevelTwo.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExerciseDayList()
            } else {
                secondAdapter.setData(exercises)
                detailList.clear()
                detailList.addAll(exercises)
                var count = 0
                for (item in exercises) {
                    if (item.isCompleted) {
                        count++
                    }
                }
                if (count == 3) {
                    binding.customProgressBar.progress = 0
                    binding.txtProgress.text = "%0"
                }else {
                    binding.customProgressBar.progress = count * 100 / 21
                    binding.txtProgress.text = "%${(count * 100 / 21).toInt()}"
                }
            }
        })
    }


    @SuppressLint("DiscouragedApi")
    private fun addToRoomExerciseDayList() {
        try {
            val inputStream = context?.assets?.open("secondday.json")
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
            secondAdapter.setData(detailList)

            var count = 0
            for (item in detailList) {
                if (item.isCompleted) {
                    count++
                }
            }
            binding.customProgressBar.progress = count * 100 / 21
            binding.txtProgress.text = "%${(count * 100 / 21).toInt()}"

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
                if (item.optInt("Level") == 2) {
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

    private fun setRecyclerview() = with(binding) {
        listDaysOfWeek.layoutManager = LinearLayoutManager(requireContext())
        secondAdapter = ExerciseDetailSecondAdapter(detailList, this@ExerciseDetailSecondFragment)
        listDaysOfWeek.adapter = secondAdapter
    }

    private fun observeListWithDayId() {
        viewModel.getExerciseListWithDay.observe(viewLifecycleOwner, Observer { exercise ->
            if (exercise != null) {
                exerciseListSend.clear()
                exerciseListSend.addAll(exercise)
            }
            val exerciseLevel = getString(R.string.middle_level)
            val exerciseArray = exerciseListSend.toTypedArray()
            val action =
                ExerciseDetailSecondFragmentDirections.actionExerciseDetailSecondFragmentToDetailDayFragment(
                    exerciseArray, selectedModel!!, exerciseLevel
                )
            view?.findNavController()?.navigate(action)
        })
    }

    override fun onExerciseItemClicked(position: Int, isLocked: Boolean) {
        if (isLocked) {
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_reward_ad, null)

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
                myRewardedAds?.showRewardAds(R.string.rewarded_ad1) {
                    selectedModel = detailList[position]
                    viewModel.updateIsCompletedToTrue(2, detailList[position].day)
                    val selectedDay = position + 1
                    viewModel.getExerciseListWithDayID(selectedDay, 1)
                    observeListWithDayId()
                }
                dialog.dismiss()
            }
        } else {
            selectedModel = detailList[position]
            val selectedDay = position + 1
            viewModel.getExerciseListWithDayID(selectedDay, 1)
            observeListWithDayId()
        }
    }


}