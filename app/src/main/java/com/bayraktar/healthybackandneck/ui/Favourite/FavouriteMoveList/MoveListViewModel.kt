package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SavedFavoritesState(
    val records: List<ExerciseDayExercise> = emptyList(),
    val loaded: Boolean = false,
    val loading: Boolean = true,
    val removing: Int? = null,
    val loadError: Boolean = false,
    val removeError: Boolean = false,
) {
    val uniqueRecords: List<ExerciseDayExercise> get() = records.distinctBy { it.exerciseId }
}

@HiltViewModel
class MoveListViewModel @Inject constructor(private val repo: RoomRepository) : ViewModel() {
    private val mutableState = MutableLiveData(SavedFavoritesState())
    val state: LiveData<SavedFavoritesState> = mutableState
    private var loadJob: Job? = null

    fun getFavExerciseList() {
        if (mutableState.value?.removing != null || loadJob?.isActive == true) return
        mutableState.value = mutableState.value!!.copy(loading = true, loadError = false)
        loadJob = viewModelScope.launch {
            try {
                val rows = withContext(Dispatchers.IO) { repo.getIsFavouriteTrue() }
                mutableState.value = SavedFavoritesState(records = rows, loaded = true, loading = false)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                mutableState.value = mutableState.value!!.copy(loading = false, loadError = true)
            }
        }
    }

    fun updateIsFavouriteToFalse(exerciseId: Int) {
        val current = mutableState.value ?: return
        if (current.loading || current.removing != null || current.records.none { it.exerciseId == exerciseId }) return
        mutableState.value = current.copy(removing = exerciseId, removeError = false)
        viewModelScope.launch {
            try {
                val rows = withContext(Dispatchers.IO) {
                    // The existing exerciseId update removes every day/level copy of this favorite.
                    repo.updateIsFavouriteToFalse(exerciseId)
                    repo.getIsFavouriteTrue()
                }
                mutableState.value = SavedFavoritesState(records = rows, loaded = true, loading = false)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                mutableState.value = mutableState.value!!.copy(removing = null, removeError = true)
            }
        }
    }
}
