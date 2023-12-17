package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMain.TabFragments.LegButt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LegButtViewModel @Inject constructor(
    private val repo: RoomRepository
):ViewModel(){


    private val _getExerciseListByTitle: MutableLiveData<List<SubExerciseDayExercise>> = MutableLiveData()
    val getExerciseListByTitle: LiveData<List<SubExerciseDayExercise>> get() = _getExerciseListByTitle

    fun getExerciseListByTitle() {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getExerciseListByTitleName(titleName = "legbutt")
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