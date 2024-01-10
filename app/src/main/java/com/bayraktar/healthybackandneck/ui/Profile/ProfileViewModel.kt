package com.bayraktar.healthybackandneck.ui.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.CountModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.MotivationNotificationState
import com.bayraktar.healthybackandneck.data.Models.WaterReminderState
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: RoomRepository
): ViewModel() {


    private val _waterReminder: MutableLiveData<WaterReminderState> = MutableLiveData()
    val waterReminder: LiveData<WaterReminderState> get() = _waterReminder



    private val _motivateNotif: MutableLiveData<MotivationNotificationState> = MutableLiveData()
    val motivateNotif: LiveData<MotivationNotificationState> get() = _motivateNotif




    fun insertMotiviateState(state: MotivationNotificationState) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveMotivationNotificationState(state)
        }
    }

    fun insertWater(state: WaterReminderState) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveWaterReminderState(state)
        }
    }

    fun getMovitivate() {
        viewModelScope.launch(Dispatchers.IO){
            val exercises  = repo.getMotivationNotificationState()
            _motivateNotif.postValue(exercises)
        }
    }

    fun getWaterReminder() {
        viewModelScope.launch(Dispatchers.IO){
            val exercisez  = repo.getWaterReminderState()
            _waterReminder.postValue(exercisez)
        }
    }



}