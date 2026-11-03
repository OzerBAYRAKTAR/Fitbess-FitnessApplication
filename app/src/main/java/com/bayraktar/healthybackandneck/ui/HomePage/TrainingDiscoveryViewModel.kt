package com.bayraktar.healthybackandneck.ui.HomePage

import androidx.lifecycle.ViewModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Repository.RoomRepository
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TrainingDiscoveryViewModel @Inject constructor(
    private val repo: RoomRepository,
    workoutRepository: WorkoutRepository,
) : ViewModel() {
    val sessions = workoutRepository.sessions

    suspend fun exercises(workout: RegionWorkout): List<ExerciseDayExercise> = withContext(Dispatchers.IO) {
        repo.getExerciseListWithTitleAndLevel(workout.category, workout.level)
    }
}
