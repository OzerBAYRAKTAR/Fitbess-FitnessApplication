package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExerciseDetailFirstVievModel @Inject constructor(
    private val repo: RoomRepository
):ViewModel() {

    private val _exerciseDayExercisesLevelOne: LiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val exerciseDayExercisesLevelOne get() = _exerciseDayExercisesLevelOne


    private val _getExerciseListWithDay: MutableLiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val getExerciseListWithDay: LiveData<List<ExerciseDayExercise>> get() = _getExerciseListWithDay



    private val _exerciseDays: LiveData<ExerciseDay> = MutableLiveData()
    val exerciseDays get() = _exerciseDays


    fun insertExerciseDay(exerciseDay: ExerciseDay) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDay(exerciseDay)
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
    }

    fun getExerciseListWithDayID(dayId: Int,level: Int) {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithDayID(dayID = dayId, level = level)
            _getExerciseListWithDay.postValue(exercises)
        }
    }

    fun fetchExerciseDayExercisesWithLevelOne() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDayExercisesWithLevelOne()
            (exerciseDayExercisesLevelOne as MutableLiveData).postValue(exercises)
        }
    }


}