package com.bayraktar.healthybackandneck.utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object MoveAccess {
    private val categories = setOf("stretch", "bellyneck", "arm", "abs", "legbutt")
    fun requiresReward(move: ExerciseDayExercise) = move.dayId == 0 && move.titleName in categories && move.level > 1
    fun allowed(move: ExerciseDayExercise, earned: Set<Int>, adFree: Boolean = false) =
        adFree || !requiresReward(move) || move.id in earned

    fun singleUnlockIds(move: ExerciseDayExercise): Set<Int> =
        if (requiresReward(move)) setOf(move.id) else emptySet()

    /** One ad unlocks every rewarded movement in only this catalog category and level. */
    fun levelUnlockIds(moves: List<ExerciseDayExercise>): Set<Int> {
        val selected = moves.firstOrNull(::requiresReward) ?: return emptySet()
        return moves.asSequence()
            .filter(::requiresReward)
            .filter { it.titleName == selected.titleName && it.level == selected.level }
            .map(ExerciseDayExercise::id)
            .toSet()
    }
}

class MoveAccessViewModel(application: Application) : AndroidViewModel(application) {
    private val store = DataStoreManage.getInstance(application)
    private val _earned = MutableStateFlow<Set<Int>?>(null)
    val earned = _earned.asStateFlow()
    val adFree = RevenueCatManager.adFree
    private val _failed = MutableStateFlow(false)
    val failed = _failed.asStateFlow()
    init {
        viewModelScope.launch {
            try { store.unlockedMoveIds().collect { _earned.value = it; _failed.value = false } }
            catch (error: Exception) {
                if (error is CancellationException) throw error
                _failed.value = true
            }
        }
    }
    // Earned rewards outlive the ad host view, including rotation during the ad.
    fun grant(move: ExerciseDayExercise) {
        val ids = MoveAccess.singleUnlockIds(move)
        if (ids.isEmpty()) return
        rewardWrites.launch {
            try { store.unlockMoves(ids); _failed.value = false }
            catch (error: Exception) {
                if (error is CancellationException) throw error
                _failed.value = true
            }
        }
    }

    fun grantLevel(moves: List<ExerciseDayExercise>) {
        val ids = MoveAccess.levelUnlockIds(moves)
        if (ids.isEmpty()) return
        rewardWrites.launch {
            try { store.unlockMoves(ids); _failed.value = false }
            catch (error: Exception) {
                if (error is CancellationException) throw error
                _failed.value = true
            }
        }
    }
    companion object {
        private val rewardWrites = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO)
    }
}
