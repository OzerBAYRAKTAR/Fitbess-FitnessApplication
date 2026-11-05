package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailSecond

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import com.bayraktar.healthybackandneck.databinding.FragmentExerciseDetailSecondBinding
import com.bayraktar.healthybackandneck.utils.Interfaces.ExerciseItemClickListener
import com.bayraktar.healthybackandneck.utils.Interfaces.RecyclerClicked
import com.bayraktar.healthybackandneck.utils.RewardedAds
import com.bayraktar.healthybackandneck.utils.RevenueCatManager
import com.bayraktar.healthybackandneck.utils.AdFreePolicy
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

@AndroidEntryPoint
class ExerciseDetailSecondFragment : Fragment(), ExerciseItemClickListener {

    private val discovery: com.bayraktar.healthybackandneck.ui.HomePage.TrainingDiscoveryViewModel by viewModels()
    private var _binding: FragmentExerciseDetailSecondBinding? = null
    val binding get() = _binding!!

    private var detailList = ArrayList<ExerciseDay>()
    private lateinit var secondAdapter: ExerciseDetailSecondAdapter

    private val viewModel: ExerciseDetailSecondViewModel by viewModels()
    private val moveAccess: com.bayraktar.healthybackandneck.utils.MoveAccessViewModel by activityViewModels()

    private val exerciseListSend = ArrayList<ExerciseDayExercise>()
    private var selectedModel: ExerciseDay? = null
    private var pendingDayId: Int? = null
    private var myRewardedAds: RewardedAds? = null
    private var seedingDayList = false
    private var seedingExercises = false
    private var appliedFocusDay = false
    private var dayMetadataRefreshStarted = false

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

        myRewardedAds = RewardedAds(requireActivity(), viewLifecycleOwner)
        myRewardedAds?.loadRewardedAds(R.string.rewarded_ad2)
        setRecyclerview()
        com.bayraktar.healthybackandneck.utils.bindWorkoutDayControls(
            viewLifecycleOwner, binding.listDaysOfWeek, listOf(binding.week1, binding.week2, binding.week3), binding.continueDay,
            target = { secondAdapter.continueTarget() },
            onContinue = { position -> onExerciseItemClicked(position, false) }
        )
        viewLifecycleOwner.lifecycleScope.launch {
            discovery.sessions.collect { sessions ->
                secondAdapter.completedDays = sessions.filter { it.level == 2 && it.isComplete }.map { it.day }.toSet()
                secondAdapter.resumeDay = sessions.filter { it.level == 2 && !it.isComplete && it.day !in secondAdapter.completedDays }.maxByOrNull { it.endedAt }?.day
                secondAdapter.notifyDataSetChanged()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            RevenueCatManager.adFree.collect {
                secondAdapter.adFree = it == true
                secondAdapter.notifyDataSetChanged()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            DataStoreManage.getInstance(requireContext().applicationContext).rewardedWorkoutDayKeys().collect { unlocked ->
                secondAdapter.rewardedWorkoutDayKeys = unlocked
                secondAdapter.notifyDataSetChanged()
            }
        }
        viewModel.exerciseCounts.observe(viewLifecycleOwner) { counts ->
            secondAdapter.exerciseCounts = counts
            secondAdapter.notifyDataSetChanged()
        }
        observeLevelTwo()
        observeDayListLevelOne()
        observeListWithDayId()
        binding.prgressStart.visibility = View.VISIBLE
        backstack()

        binding.imageBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }

        viewModel.fetchExerciseDayExercisesWithLevelTwo()

        viewModel.fetchExerciseDayListWithLevelTwo()
    }

    private fun backstack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view?.findNavController()?.navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun observeLevelTwo() {
        viewModel.exerciseDayExercisesLevelTwo.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises == false) {
                addToRoomExercises()
            } else if (exercises == true) {
                viewModel.fetchExerciseCounts()
            }
        })
    }

    private fun observeDayListLevelOne() {
        viewModel.exerciseDayLevelTwo.observe(viewLifecycleOwner, Observer { exercises ->
            if (exercises.isEmpty()) {
                addToRoomExerciseDayList()
            } else {
                val orderedDays = exercises.sortedBy { it.day }
                secondAdapter.setData(orderedDays)
                detailList.clear()
                detailList.addAll(orderedDays)
                applyRequestedDayFocus()
                binding.prgressStart.visibility = View.GONE
                refreshDayMetadata(exercises)
            }
        })
    }

    private fun refreshDayMetadata(storedDays: List<ExerciseDay>) {
        if (dayMetadataRefreshStarted) return
        dayMetadataRefreshStarted = true
        val parserContext = requireContext().applicationContext.createConfigurationContext(resources.configuration)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val currentCatalog = withContext(Dispatchers.IO) {
                    WorkoutCatalogParser.readDays(parserContext, "secondday.json")
                }
                val refreshed = WorkoutCatalogParser.mergeDayMetadata(currentCatalog, storedDays)
                if (refreshed != storedDays) viewModel.publishRefreshedDayMetadata(refreshed)
            } catch (error: CancellationException) {
                dayMetadataRefreshStarted = false
                throw error
            } catch (error: Exception) {
                dayMetadataRefreshStarted = false
                error.printStackTrace()
            }
        }
    }


    @SuppressLint("DiscouragedApi")
    private fun addToRoomExerciseDayList() {
        if (seedingDayList) return
        seedingDayList = true
        val parserContext = requireContext().applicationContext.createConfigurationContext(resources.configuration)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val days = withContext(Dispatchers.IO) { WorkoutCatalogParser.readDays(parserContext, "secondday.json") }
                if (_binding == null) return@launch
                detailList.clear()
                detailList.addAll(days)
                viewModel.insertExerciseDaysList(days)
                secondAdapter.setData(days)
                applyRequestedDayFocus()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                error.printStackTrace()
            } finally {
                seedingDayList = false
                _binding?.prgressStart?.visibility = View.GONE
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun addToRoomExercises() {
        if (seedingExercises) return
        seedingExercises = true
        val parserContext = requireContext().applicationContext.createConfigurationContext(resources.configuration)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val exercises = withContext(Dispatchers.IO) { WorkoutCatalogParser.readExercises(parserContext, level = 2) }
                viewModel.insertExerciseDayExercise(exercises)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                error.printStackTrace()
            } finally {
                seedingExercises = false
            }
        }
    }
    private fun setRecyclerview() = with(binding) {
        listDaysOfWeek.layoutManager = LinearLayoutManager(requireContext())
        secondAdapter = ExerciseDetailSecondAdapter(detailList, this@ExerciseDetailSecondFragment)
        listDaysOfWeek.adapter = secondAdapter
    }

    private fun applyRequestedDayFocus() {
        if (appliedFocusDay || detailList.isEmpty()) return
        appliedFocusDay = true
        val requestedDay = requireArguments().getInt("focusDay", 0)
        val position = detailList.indexOfFirst { it.day == requestedDay }
        if (position >= 0) binding.listDaysOfWeek.post {
            if (_binding != null) (binding.listDaysOfWeek.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(position, 0)
        }
    }
    private fun observeListWithDayId() {
        viewModel.getExerciseListWithDay.observe(viewLifecycleOwner, Observer { result ->
            val selectedDay = selectedModel ?: return@Observer
            if (result == null || pendingDayId != result.first) return@Observer
            val navController = view?.findNavController() ?: return@Observer
            if (navController.currentDestination?.id != R.id.exerciseDetailSecondFragment) return@Observer

            exerciseListSend.clear()
            exerciseListSend.addAll(result.second)
            val exerciseLevel = getString(R.string.middle_level)
            val exerciseArray = exerciseListSend.toTypedArray()
            val action =
                ExerciseDetailSecondFragmentDirections.actionExerciseDetailSecondFragmentToDetailDayFragment(
                    exerciseArray, selectedDay, exerciseLevel
                )
            selectedModel = null
            pendingDayId = null
            navController.navigate(action)
        })
    }

    override fun onExerciseItemClicked(position: Int, isLocked: Boolean) {
        if (AdFreePolicy.entitlementPendingForLockedMove(isLocked, RevenueCatManager.adFree.value)) {
            Toast.makeText(requireContext(), R.string.access_loading, Toast.LENGTH_SHORT).show()
            return
        }
        if (isLocked && RevenueCatManager.adFree.value != true) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.unlock_workout_title)
                .setMessage(R.string.unlock_workout_choice_message)
                .setPositiveButton(R.string.watch_unlock) { _, _ ->
                    val rewardPersistence = moveAccess
                    myRewardedAds?.showRewardAds(retainEarnedReward = true) {
                        val selected = detailList.getOrNull(position) ?: return@showRewardAds
                        rewardPersistence.grantWorkoutDay(selected.level, selected.day) {
                            if (!isAdded || _binding == null) return@grantWorkoutDay
                            selectedModel = selected
                            val selectedDay = selected.dayId
                            pendingDayId = selectedDay
                            viewModel.getExerciseListWithDayID(selectedDay, selected.level)
                        }
                    }
                }
                .setNeutralButton(R.string.remove_ads) { _, _ ->
                    view?.findNavController()?.navigate(R.id.action_global_adFreeOfferFragment)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        } else {
            selectedModel = detailList[position]
            val selectedDay = detailList[position].dayId
            pendingDayId = selectedDay
            viewModel.getExerciseListWithDayID(selectedDay, detailList[position].level)
        }
    }


    override fun onDestroyView() {
        myRewardedAds = null
        dayMetadataRefreshStarted = false
        _binding = null
        super.onDestroyView()
    }
}
