package com.bayraktar.healthybackandneck.ui.ExerciseMovesEnd

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseMovesEndViewModel @Inject constructor(
    private val repo: RoomRepository
): ViewModel() {


    fun updateIsCompletedToTrue(level: Int,day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateIsCompletedTrue(level = level, day = day)
        }
    }

    fun updatecount() {
        viewModelScope.launch(Dispatchers.IO) {
            val rowsAffected = repo.updateCount()
            Log.d("UpdateCount", "Rows affected: $rowsAffected")        }
    }

}