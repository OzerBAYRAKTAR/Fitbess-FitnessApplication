package com.bayraktar.healthybackandneck.data.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise
import retrofit2.http.GET


@Dao
interface MovesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDay(day: ExerciseDay)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDayExercise(dayExercise: List<ExerciseDayExercise>)

    @Query("SELECT COUNT(*) FROM exercise_day_exercise WHERE level = 1")
    fun getExerciseCountForLevelOne(): Int

    @Query("SELECT * FROM ExerciseDay")
    fun getExerciseDays(): ExerciseDay

    @Query("SELECT * FROM exercise_day_exercise WHERE level = 1")
    fun getExerciseDayExercisesWithLevelOne(): List<ExerciseDayExercise>

    @Transaction
    @Query("Select * from exercise_day_exercise where dayId = :dayId")
    fun getExerciseListWithDayID(dayId: Int): List<ExerciseDayExercise>

    @Transaction
    @Query("Select * from ExerciseDay where dayId = :dayId")
    fun getExerciseDayWithExerciseDayExercise(dayId: Int): List<ExerciseDayWithExerciseDayExercise>

}
