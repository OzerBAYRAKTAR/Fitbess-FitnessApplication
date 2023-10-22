package com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem


data class ExerciseDayWithExerciseDayExercise(
    @Embedded val exerciseDay: ExerciseDay,
    @Relation(
        parentColumn = "dayId",
        entityColumn = "dayId"
    )
    val exerciseDaysExercise: List<ExerciseDayExercise>
)