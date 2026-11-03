package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailSecond

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
class ExerciseDetailSecondViewModel @Inject constructor(
    private val repo: RoomRepository,
    workoutRepository: WorkoutRepository
):ViewModel() {

    private val _exerciseDayExercisesLevelTwo = MutableLiveData<Boolean>()
    val exerciseDayExercisesLevelTwo get() = _exerciseDayExercisesLevelTwo

    val sessions = workoutRepository.sessions



    private val _getExerciseListWithDay = MutableLiveData<Pair<Int, List<ExerciseDayExercise>>>()
    val getExerciseListWithDay: LiveData<Pair<Int, List<ExerciseDayExercise>>> get() = _getExerciseListWithDay
    private val dayExerciseCache = mutableMapOf<Int, List<ExerciseDayExercise>>()


    private val _exerciseDays: LiveData<ExerciseDay> = MutableLiveData()
    val exerciseDays get() = _exerciseDays


    private val _exerciseDayLevelTwo = MutableLiveData<List<ExerciseDay>>()
    val exerciseDayLevelTwo get() = _exerciseDayLevelTwo

    private val _exerciseCounts = MutableLiveData<Map<Int, Int>>()
    val exerciseCounts: LiveData<Map<Int, Int>> get() = _exerciseCounts



    fun insertExerciseDaysList(exerciseDayList: List<ExerciseDay>) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDaysList(exerciseDayList)
        _exerciseDayLevelTwo.postValue(exerciseDayList.sortedBy(ExerciseDay::day))
    }

    fun publishRefreshedDayMetadata(days: List<ExerciseDay>) {
        _exerciseDayLevelTwo.postValue(days.sortedBy(ExerciseDay::day))
    }


    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
        _exerciseDayExercisesLevelTwo.postValue(true)
        loadExerciseCounts()
    }

    fun fetchExerciseCounts() {
        if (_exerciseCounts.value != null) return
        loadExerciseCounts()
    }

    private fun loadExerciseCounts() = viewModelScope.launch(Dispatchers.IO) {
        _exerciseCounts.postValue(repo.getExerciseCountsByDay(level = 2, titleName = "noTitle")
            .associate { it.dayId to it.exerciseCount })
    }

    fun getExerciseListWithDayID(dayId: Int,level: Int) {
        synchronized(dayExerciseCache) { dayExerciseCache[dayId] }?.let {
            _getExerciseListWithDay.value = dayId to it
            return
        }
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithDayID(dayID = dayId,level = level, titleName = "noTitle")
            if (exercises.isNotEmpty()) synchronized(dayExerciseCache) { dayExerciseCache[dayId] = exercises }
            _getExerciseListWithDay.postValue(dayId to exercises)
        }
    }

    fun fetchExerciseDayExercisesWithLevelTwo() {
        if (_exerciseDayExercisesLevelTwo.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exists = repo.hasExerciseDayExercisesWithLevelTwo(titleName = "noTitle")
            exerciseDayExercisesLevelTwo.postValue(exists)
        }
    }

    fun fetchExerciseDayListWithLevelTwo() {
        if (_exerciseDayLevelTwo.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDaysLevel2()
            _exerciseDayLevelTwo.postValue(exercises)
        }
    }

    fun updateIsCompletedToTrue(level: Int,day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsCompletedTrue(level = level, day = day)
        }
    }
}
