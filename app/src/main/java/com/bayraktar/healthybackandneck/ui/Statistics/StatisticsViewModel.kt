package com.bayraktar.healthybackandneck.ui.Statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.CountModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repo: RoomRepository
): ViewModel() {


    private val _getCount: MutableLiveData<CountModel> = MutableLiveData()
    val getCount: LiveData<CountModel> get() = _getCount





    fun getCountFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.getCount(1)
            _getCount.postValue(response)
        }
    }

}