package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailThird

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
class ExerciseDetailThirdViewModel  @Inject constructor(
    private val repo: RoomRepository
):ViewModel() {

    private val _exerciseDayExercisesLevelThird: LiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val exerciseDayExercisesLevelThird get() = _exerciseDayExercisesLevelThird


    private val _getExerciseListWithDay: MutableLiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val getExerciseListWithDay: LiveData<List<ExerciseDayExercise>> get() = _getExerciseListWithDay


    private val _exerciseDayLevelThird: LiveData<List<ExerciseDay>> = MutableLiveData()
    val exerciseDayLevelThird get() = _exerciseDayLevelThird



    private val _exerciseDays: LiveData<ExerciseDay> = MutableLiveData()
    val exerciseDays get() = _exerciseDays



    fun insertExerciseDaysList(exerciseDayList: List<ExerciseDay>) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDaysList(exerciseDayList)
    }

    fun insertExerciseDay(exerciseDay: ExerciseDay) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDay(exerciseDay)
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(
        Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
    }

    fun getExerciseListWithDayID(dayId: Int,level : Int) {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithDayID(dayID = dayId,level = level, titleName = "noTitle")
            _getExerciseListWithDay.postValue(exercises)
        }
    }

    fun fetchExerciseDayExercisesWithLevelThird() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDayExercisesWithLevelThird(titleName = "noTitle")
            (exerciseDayExercisesLevelThird as MutableLiveData).postValue(exercises)
        }
    }

    fun fetchExerciseDayListWithLevelThird() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getExerciseDaysLevel3()
            (_exerciseDayLevelThird as MutableLiveData).postValue(exercises)
        }
    }


}