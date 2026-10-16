package com.bayraktar.healthybackandneck.ui.ExerciseMoves

import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** Activity-owned session survives fragment transitions; saved state never stores a running clock. */
@HiltViewModel
class WorkoutSessionViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: WorkoutRepository,
) : ViewModel() {
    private var runningSince: Long? = null
    private val state: Bundle get() = savedState.get<Bundle>("workout") ?: Bundle()
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()
    sealed interface SaveState {
        data object Idle : SaveState
        data object Saving : SaveState
        data class Saved(val session: WorkoutSession) : SaveState
        data object Failed : SaveState
    }

    fun begin(day: ExerciseDay?, count: Int, title: String) {
        runningSince = null
        savedState["workout"] = Bundle().apply {
            putString("id", UUID.randomUUID().toString())
            putLong("start", System.currentTimeMillis())
            putInt("count", count)
            putInt("level", day?.level ?: 0)
            putInt("day", day?.day ?: 0)
            putString("title", title)
            putIntegerArrayList("completed", arrayListOf())
        }
        _saveState.value = SaveState.Idle
    }

    fun resume() {
        if (state.containsKey("id") && !state.containsKey("end") && runningSince == null) {
            runningSince = SystemClock.elapsedRealtime()
        }
    }

    fun pause() {
        val start = runningSince ?: return
        runningSince = null
        savedState["workout"] = Bundle(state).apply {
            putLong("active", getLong("active") + (SystemClock.elapsedRealtime() - start).coerceAtLeast(0))
        }
    }

    fun completeMove(index: Int) {
        savedState["workout"] = Bundle(state).apply {
            val completed = getIntegerArrayList("completed") ?: arrayListOf()
            if (index in 0 until getInt("count") && index !in completed) completed.add(index)
            putIntegerArrayList("completed", completed)
        }
    }

    fun finish() {
        pause()
        val current = state
        if (!current.containsKey("id") || _saveState.value is SaveState.Saving) return
        if (!current.containsKey("end")) {
            current.putLong("end", System.currentTimeMillis())
            savedState["workout"] = current
        }
        val session = WorkoutSession(
            current.getString("id")!!, current.getLong("start"), current.getLong("end"),
            current.getLong("active"), current.getIntegerArrayList("completed")?.size ?: 0,
            current.getInt("count"), current.getInt("level"), current.getInt("day"), current.getString("title").orEmpty(),
        )
        _saveState.value = SaveState.Saving
        viewModelScope.launch {
            try {
                repository.save(session)
                _saveState.value = SaveState.Saved(session)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                android.util.Log.e("WorkoutSession", "Could not save workout", error)
                _saveState.value = SaveState.Failed
            }
        }
    }
}
