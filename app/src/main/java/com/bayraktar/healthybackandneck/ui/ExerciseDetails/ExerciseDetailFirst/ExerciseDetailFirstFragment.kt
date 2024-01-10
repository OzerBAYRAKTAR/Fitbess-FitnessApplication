package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDaysOfWeekBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.ExerciseItemClickListener
import com.bayraktar.healthybackandneck.utils.RewardedAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import java.nio.charset.Charset


@AndroidEntryPoint
class ExerciseDetailFirstFragment : Fragment(), ExerciseItemClickListener {

    private var _binding: FragmentExerciseDaysOfWeekBinding? = null
    val binding get() = _binding!!
    private val viewModel: ExerciseDetailFirstVievModel by viewModels()


    private var detailList = ArrayList<ExerciseDay>()
    private val exerciseDayExercise = mutableListOf<ExerciseDayExercise>()
    private val exerciseDay = mutableListOf<ExerciseDay>()
    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private lateinit var firstAdapter: ExerciseDetailFirstAdapter
    private var selectedModel: ExerciseDay? = null

    private var myRewardedAds: RewardedAds? = null
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

        //ca-app-pub-3940256099942544/5224354917 test
        MobileAds.initialize(requireContext()) {}


        myRewardedAds = RewardedAds(requireActivity())
        myRewardedAds?.loadRewardedAds(R.string.rewarded_ad1)
        setRecyclerview()
        observeLevelOne()
        observeDayListLevelOne()

        backstack()
        viewModel.fetchExerciseDayExercisesWithLevelOne()


        viewModel.fetchExerciseDayListWithLevelOne()




        binding.imageBack.setOnClickListener {
            val action =
                ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToIdHomepageFragment()
            view.findNavController().navigate(action)
        }

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

    private fun observeDayListLevelOne() {
        viewModel.exerciseDayLevelOne.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExerciseDayList()
            } else {
                firstAdapter.setData(exercises)
                detailList.clear()
                detailList.addAll(exercises)

                var count = 0
                for (item in exercises) {
                    if (item.isCompleted) {
                        count++
                    }
                }
                binding.customProgressBar.progress = count * 100 / 21
                binding.txtProgress.text = "%${(count * 100 / 21).toInt()}"
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun addToRoomExerciseDayList() {
        try {
            val inputStream = context?.assets?.open("firstday.json")
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
            firstAdapter.setData(detailList)

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

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToIdHomepageFragment()
                view?.findNavController()?.navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
        firstAdapter = ExerciseDetailFirstAdapter(detailList, this@ExerciseDetailFirstFragment)
        listDaysOfWeek.adapter = firstAdapter
    }


    //day ve level id ile verileri getirip bir sonraki sayfaya aktarıyor
    private fun observeListWithDayId() {
        viewModel.getExerciseListWithDay.observe(viewLifecycleOwner, Observer { exercise ->
            if (exercise != null) {
                exerciseListSend.clear()
                exerciseListSend.addAll(exercise)
            }
            val exerciseLevel = getString(R.string.easy_level_fil)
            val exerciseArray = exerciseListSend.toTypedArray()
            val action =
                ExerciseDetailFirstFragmentDirections.actionExerciseDetailFirstFragmentToDetailDayFragment(
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
                    viewModel.updateIsCompletedToTrue(1, detailList[position].day)
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