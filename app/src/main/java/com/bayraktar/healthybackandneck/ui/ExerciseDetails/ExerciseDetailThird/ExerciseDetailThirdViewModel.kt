package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailThird

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
class ExerciseDetailThirdViewModel  @Inject constructor(
    private val repo: RoomRepository,
    workoutRepository: WorkoutRepository
):ViewModel() {

    private val _exerciseDayExercisesLevelThird = MutableLiveData<Boolean>()
    val exerciseDayExercisesLevelThird get() = _exerciseDayExercisesLevelThird

    val sessions = workoutRepository.sessions


    private val _getExerciseListWithDay = MutableLiveData<Pair<Int, List<ExerciseDayExercise>>>()
    val getExerciseListWithDay: LiveData<Pair<Int, List<ExerciseDayExercise>>> get() = _getExerciseListWithDay
    private val dayExerciseCache = mutableMapOf<Int, List<ExerciseDayExercise>>()


    private val _exerciseDayLevelThird = MutableLiveData<List<ExerciseDay>>()
    val exerciseDayLevelThird get() = _exerciseDayLevelThird

    private val _exerciseCounts = MutableLiveData<Map<Int, Int>>()
    val exerciseCounts: LiveData<Map<Int, Int>> get() = _exerciseCounts



    private val _exerciseDays: LiveData<ExerciseDay> = MutableLiveData()
    val exerciseDays get() = _exerciseDays



    fun insertExerciseDaysList(exerciseDayList: List<ExerciseDay>) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDaysList(exerciseDayList)
        _exerciseDayLevelThird.postValue(exerciseDayList.sortedBy(ExerciseDay::day))
    }

    fun publishRefreshedDayMetadata(days: List<ExerciseDay>) {
        _exerciseDayLevelThird.postValue(days.sortedBy(ExerciseDay::day))
    }

    fun insertExerciseDay(exerciseDay: ExerciseDay) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDay(exerciseDay)
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(
        Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
        _exerciseDayExercisesLevelThird.postValue(true)
        loadExerciseCounts()
    }

    fun fetchExerciseCounts() {
        if (_exerciseCounts.value != null) return
        loadExerciseCounts()
    }

    private fun loadExerciseCounts() = viewModelScope.launch(Dispatchers.IO) {
        _exerciseCounts.postValue(repo.getExerciseCountsByDay(level = 3, titleName = "noTitle")
            .associate { it.dayId to it.exerciseCount })
    }

    fun getExerciseListWithDayID(dayId: Int,level : Int) {
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

    fun fetchExerciseDayExercisesWithLevelThird() {
        if (_exerciseDayExercisesLevelThird.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exists = repo.hasExerciseDayExercisesWithLevelThree(titleName = "noTitle")
            exerciseDayExercisesLevelThird.postValue(exists)
        }
    }

    fun fetchExerciseDayListWithLevelThird() {
        if (_exerciseDayLevelThird.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDaysLevel3()
            _exerciseDayLevelThird.postValue(exercises)
        }
    }

    fun updateIsCompletedToTrue(level: Int,day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsCompletedTrue(level = level, day = day)
        }
    }
}
