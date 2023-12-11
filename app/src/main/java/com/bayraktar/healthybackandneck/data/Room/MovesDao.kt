package com.bayraktar.healthybackandneck.data.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import retrofit2.http.GET


@Dao
interface MovesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDay(day: ExerciseDay)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExerciseDayExercise(dayExercise: List<ExerciseDayExercise>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubExerciseDayExercise(subExercise: List<SubExerciseDayExercise>)


    @Query("SELECT COUNT(*) FROM exercise_day_exercise WHERE level = 1")
    fun getExerciseCountForLevelOne(): Int

    @Query("SELECT * FROM ExerciseDay")
    fun getExerciseDays(): ExerciseDay

    @Query("SELECT * FROM sub_exercise_day_exercise")
    fun getSubExerciseDayExercises(): List<SubExerciseDayExercise>


    @Query("SELECT * FROM exercise_day_exercise WHERE level = 1")
    fun getExerciseDayExercisesWithLevelOne(): List<ExerciseDayExercise>

    @Query("SELECT * FROM exercise_day_exercise WHERE level = 2")
    fun getExerciseDayExercisesWithLevelTwo(): List<ExerciseDayExercise>

    @Query("SELECT * FROM exercise_day_exercise WHERE level = 3")
    fun getExerciseDayExercisesWithLevelThird(): List<ExerciseDayExercise>

    @Transaction
    @Query("Select * from exercise_day_exercise where dayId = :dayId and level = :level")
    fun getExerciseListWithDayID(dayId: Int,level:Int): List<ExerciseDayExercise>


    @Transaction
    @Query("Select * from sub_exercise_day_exercise where titleName = :titleName")
    fun getExerciseListByTitleName(titleName: String): List<SubExerciseDayExercise>



    @Transaction
    @Query("Select * from sub_exercise_day_exercise where titleName = :titleName and level = :level")
    fun getExerciseListWithTitleAndLEvel(titleName: String,level:Int): List<SubExerciseDayExercise>

    @Transaction
    @Query("Select * from ExerciseDay where dayId = :dayId")
    fun getExerciseDayWithExerciseDayExercise(dayId: Int): List<ExerciseDayWithExerciseDayExercise>

    @Transaction
    @Query("UPDATE sub_exercise_day_exercise SET isFavourite = 1 Where exerciseId = :exerciseId")
    fun updateIsFavourite(exerciseId: Int)

    @Transaction
    @Query("UPDATE sub_exercise_day_exercise SET isFavourite = 0 Where exerciseId = :exerciseId")
    fun updateIsFavouriteToFalse(exerciseId: Int)

    @Transaction
    @Query("Select * from  sub_exercise_day_exercise Where isFavourite = 1")
    fun getIsFavouriteTrue():List<SubExerciseDayExercise>


}
