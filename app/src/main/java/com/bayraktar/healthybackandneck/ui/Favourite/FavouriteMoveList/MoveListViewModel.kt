package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

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
class MoveListViewModel @Inject constructor(
    private val repo: RoomRepository
): ViewModel() {


    private val _getFavExercises: MutableLiveData<List<ExerciseDayExercise>> = MutableLiveData()
    val getFavExercises: LiveData<List<ExerciseDayExercise>> get() = _getFavExercises



    fun getFavExerciseList() {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getIsFavouriteTrue()
            _getFavExercises.postValue(exercises)
        }
    }

    fun updateIsFavouriteToFalse(exerciseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsFavouriteToFalse(exerciseId = exerciseId)
        }
    }

}