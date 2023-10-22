package com.bayraktar.healthybackandneck.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise


@Dao
interface MovesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDay(day: ExerciseDay)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDayExercise(dayExercise: List<ExerciseDayExercise>)


    @Transaction
    @Query("Select * from ExerciseDay where dayId = :dayId")
    fun getExerciseDayWithExerciseDayExercise(dayId: Int): List<ExerciseDayWithExerciseDayExercise>

}
