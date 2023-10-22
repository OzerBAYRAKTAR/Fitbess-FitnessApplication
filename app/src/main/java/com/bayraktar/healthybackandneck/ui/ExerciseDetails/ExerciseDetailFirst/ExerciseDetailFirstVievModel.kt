package com.bayraktar.healthybackandneck.ui.ExerciseDetails.ExerciseDetailFirst

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

    fun insertExerciseDay(exerciseDay: ExerciseDay) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDay(exerciseDay)
    }

    fun insertExerciseDayExercise(exerciseDayExercise: List<ExerciseDayExercise>)= viewModelScope.launch(Dispatchers.IO) {
        repo.insertExerciseDayExercise(exerciseDayExercise)
    }
}