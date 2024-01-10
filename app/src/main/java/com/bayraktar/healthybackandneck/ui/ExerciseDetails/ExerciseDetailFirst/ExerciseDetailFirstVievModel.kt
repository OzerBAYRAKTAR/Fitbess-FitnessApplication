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


    private val _exerciseDayLevelOne: LiveData<List<ExerciseDay>> = MutableLiveData()
    val exerciseDayLevelOne get() = _exerciseDayLevelOne


    private val _getExerciseListWithDay: MutableLiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val getExerciseListWithDay: LiveData<List<ExerciseDayExercise>> get() = _getExerciseListWithDay



    private val _exerciseDaysList: LiveData<List<ExerciseDay>> = MutableLiveData()
    val exerciseDaysList get() = _exerciseDaysList


    fun insertExerciseDaysList(exerciseDayList: List<ExerciseDay>) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDaysList(exerciseDayList)
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
    }

    fun getExerciseListWithDayID(dayId: Int,level: Int) {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithDayID(dayID = dayId, level = level, titleName = "noTitle")
            _getExerciseListWithDay.postValue(exercises)
        }
    }

    fun fetchExerciseDayExercisesWithLevelOne() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDayExercisesWithLevelOne(titleName = "noTitle")
            (exerciseDayExercisesLevelOne as MutableLiveData).postValue(exercises)
        }
    }

    fun fetchExerciseDayListWithLevelOne() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDaysLevel1()
            (_exerciseDayLevelOne as MutableLiveData).postValue(exercises)
        }
    }

    fun updateIsCompletedToTrue(level: Int,day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsCompletedTrue(level = level, day = day)
        }
    }



}