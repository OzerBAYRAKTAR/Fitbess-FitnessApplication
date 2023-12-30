package com.bayraktar.healthybackandneck.data.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise


@Dao
interface MovesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDay(day: ExerciseDay)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDayExercise(dayExercise: List<ExerciseDayExercise>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDaysList(exerciseDay: List<ExerciseDay>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubExerciseDayExercise(exercise: List<ExerciseDayExercise>)


    @Query("SELECT * FROM exercise_days WHERE level = 1")
    fun getExerciseDaysLevel1(): List<ExerciseDay>


    @Query("SELECT * FROM exercise_days WHERE level = 2")
    fun getExerciseDaysLevel2(): List<ExerciseDay>


    @Query("SELECT * FROM exercise_days WHERE level = 3")
    fun getExerciseDaysLevel3(): List<ExerciseDay>


    @Query("SELECT * FROM exercise_day_exercise where titleName != :titleName")
    fun getSubExerciseDayExercises(titleName: String): List<ExerciseDayExercise>

    @Query("SELECT * FROM exercise_day_exercise WHERE level = 1 and titleName = :titleName")
    fun getExerciseDayExercisesWithLevelOne(titleName: String): List<ExerciseDayExercise>


    @Query("SELECT * FROM exercise_day_exercise WHERE level = 2 and titleName = :titleName")
    fun getExerciseDayExercisesWithLevelTwo(titleName: String): List<ExerciseDayExercise>


    @Query("SELECT * FROM exercise_day_exercise WHERE level = 3 and titleName = :titleName")
    fun getExerciseDayExercisesWithLevelThird(titleName: String): List<ExerciseDayExercise>

    @Transaction
    @Query("Select * from exercise_day_exercise where dayId = :dayId and level = :level and titleName = :titleName")
    fun getExerciseListWithDayID(dayId: Int,level:Int,titleName: String): List<ExerciseDayExercise>


    @Transaction
    @Query("Select * from exercise_day_exercise where titleName = :titleName")
    fun getExerciseListByTitleName(titleName: String): List<ExerciseDayExercise>



    @Transaction
    @Query("Select * from exercise_day_exercise where titleName = :titleName and level = :level")
    fun getExerciseListWithTitleAndLEvel(titleName: String,level:Int): List<ExerciseDayExercise>


    @Transaction
    @Query("UPDATE exercise_day_exercise SET isFavourite = 1 Where exerciseId = :exerciseId")
    fun updateIsFavourite(exerciseId: Int)

    @Transaction
    @Query("UPDATE exercise_day_exercise SET isFavourite = 0 Where exerciseId = :exerciseId")
    fun updateIsFavouriteToFalse(exerciseId: Int)

    @Transaction
    @Query("Select * from  exercise_day_exercise Where isFavourite = 1")
    fun getIsFavouriteTrue():List<ExerciseDayExercise>


    @Transaction
    @Query("UPDATE exercise_days SET isCompleted = 1 where level = :level and day = :day ")
    fun updateIsCompletedTrue(level: Int,day: Int)


}
