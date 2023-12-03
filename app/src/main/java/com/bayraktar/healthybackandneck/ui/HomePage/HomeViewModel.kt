package com.bayraktar.healthybackandneck.ui.HomePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: RoomRepository
): ViewModel() {

    private val _exerciseDayExercises: LiveData<List<SubExerciseDayExercise>> = MutableLiveData()
    val exerciseDayExercises get() = _exerciseDayExercises


    private val _getExerciseListWithLevelAndTitle: MutableLiveData<List<SubExerciseDayExercise>> = MutableLiveData()
    val getExerciseListWithLevelAndTitle: LiveData<List<SubExerciseDayExercise>> get() = _getExerciseListWithLevelAndTitle



    private val _exerciseDays: LiveData<ExerciseDay> = MutableLiveData()
    val exerciseDays get() = _exerciseDays



    fun insertExerciseDayExercise(exerciseDayExercise: List<SubExerciseDayExercise>)= viewModelScope.launch(
        Dispatchers.IO) {
        repo.insertSubExerciseDayExercise(exerciseDayExercise)
    }

    fun getExerciseListWithLevelAndTitle(titleName: String,level: Int) {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListWithTitleAndLevel(titleName = titleName, level = level)
            _getExerciseListWithLevelAndTitle.postValue(exercises)
        }
    }

    fun fetchExerciseDayExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = repo.getSubExerciseDayExercises()
            (exerciseDayExercises as MutableLiveData).postValue(exercises)
        }
    }


}