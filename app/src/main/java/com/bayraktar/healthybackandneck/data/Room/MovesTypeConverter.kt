package com.bayraktar.healthybackandneck.data.Room

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay


@TypeConverters
class MovesTypeConverter {

    @TypeConverter
    fun fromExerciseDayToString(exerciseDay: ExerciseDay?): String? {
        return exerciseDay?.toJson() // Convert the ExerciseDay object to a JSON string
    }

    @TypeConverter
    fun fromStringToExerciseDay(json: String?): ExerciseDay? {
        return json?.let { ExerciseDay.fromJson(it) } // Convert the JSON string back to an ExerciseDay object
    }
}