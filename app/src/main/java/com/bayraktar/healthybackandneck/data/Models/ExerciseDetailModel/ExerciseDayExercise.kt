package com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "exercise_day_exercise")
data class ExerciseDayExercise(
    @PrimaryKey(autoGenerate = false)
    val exerciseId: Int ,
    val dayId: Int,
    val exerciseName: String,
    val exerciseDescription: String,
    val image: Int,
    var isExerciseCompleted: Boolean,
    var level: Int
)


