package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.Arm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArmViewModel @Inject constructor(
    private val repo: RoomRepository
):ViewModel(){


    private val _getExerciseListByTitle: MutableLiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val getExerciseListByTitle: LiveData<List<ExerciseDayExercise>> get() = _getExerciseListByTitle

    fun getExerciseListByTitle() {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListByTitleName(titleName = "arm")
            _getExerciseListByTitle.postValue(exercises)
        }
    }

    fun updateExerciseById(exerciseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsFavourite(exerciseId = exerciseId)
        }
    }

    fun updateIsFavouriteToFalse(exerciseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsFavouriteToFalse(exerciseId = exerciseId)
        }
    }
}