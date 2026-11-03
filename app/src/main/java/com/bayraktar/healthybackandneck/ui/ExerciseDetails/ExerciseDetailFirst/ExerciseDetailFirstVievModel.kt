package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExerciseDetailFirstVievModel @Inject constructor(
    private val repo: RoomRepository,
    workoutRepository: WorkoutRepository
):ViewModel() {


    private val _exerciseDayExercisesLevelOne = MutableLiveData<Boolean>()
    val exerciseDayExercisesLevelOne get() = _exerciseDayExercisesLevelOne

    val sessions = workoutRepository.sessions


    private val _exerciseDayLevelOne = MutableLiveData<List<ExerciseDay>>()
    val exerciseDayLevelOne get() = _exerciseDayLevelOne

    private val _exerciseCounts = MutableLiveData<Map<Int, Int>>()
    val exerciseCounts: LiveData<Map<Int, Int>> get() = _exerciseCounts


    private val _getExerciseListWithDay = MutableLiveData<Pair<Int, List<ExerciseDayExercise>>>()
    val getExerciseListWithDay: LiveData<Pair<Int, List<ExerciseDayExercise>>> get() = _getExerciseListWithDay
    private val dayExerciseCache = mutableMapOf<Int, List<ExerciseDayExercise>>()



    private val _exerciseDaysList: LiveData<List<ExerciseDay>> = MutableLiveData()
    val exerciseDaysList get() = _exerciseDaysList


    fun insertExerciseDaysList(exerciseDayList: List<ExerciseDay>) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDaysList(exerciseDayList)
        _exerciseDayLevelOne.postValue(exerciseDayList.sortedBy(ExerciseDay::day))
    }

    fun publishRefreshedDayMetadata(days: List<ExerciseDay>) {
        _exerciseDayLevelOne.postValue(days.sortedBy(ExerciseDay::day))
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
        _exerciseDayExercisesLevelOne.postValue(true)
        loadExerciseCounts()
    }

    fun fetchExerciseCounts() {
        if (_exerciseCounts.value != null) return
        loadExerciseCounts()
    }

    private fun loadExerciseCounts() = viewModelScope.launch(Dispatchers.IO) {
        _exerciseCounts.postValue(repo.getExerciseCountsByDay(level = 1, titleName = "noTitle")
            .associate { it.dayId to it.exerciseCount })
    }

    fun getExerciseListWithDayID(dayId: Int,level: Int) {
        synchronized(dayExerciseCache) { dayExerciseCache[dayId] }?.let {
            _getExerciseListWithDay.value = dayId to it
            return
        }
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithDayID(dayID = dayId, level = level, titleName = "noTitle")
            if (exercises.isNotEmpty()) synchronized(dayExerciseCache) { dayExerciseCache[dayId] = exercises }
            _getExerciseListWithDay.postValue(dayId to exercises)
        }
    }

    fun fetchExerciseDayExercisesWithLevelOne() {
        if (_exerciseDayExercisesLevelOne.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exists = repo.hasExerciseDayExercisesWithLevelOne(titleName = "noTitle")
            exerciseDayExercisesLevelOne.postValue(exists)
        }
    }

    fun fetchExerciseDayListWithLevelOne() {
        if (_exerciseDayLevelOne.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDaysLevel1()
            _exerciseDayLevelOne.postValue(exercises)
        }
    }

    fun updateIsCompletedToTrue(level: Int,day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsCompletedTrue(level = level, day = day)
        }
    }



}
