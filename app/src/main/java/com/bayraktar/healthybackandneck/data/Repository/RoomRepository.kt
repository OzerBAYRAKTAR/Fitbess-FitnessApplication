package com.bayraktar.healthybackandneck.data.Repository

import androidx.lifecycle.LiveData
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Room.MovesDao
import javax.inject.Inject


class RoomRepository @Inject constructor(private val movesDao: MovesDao) {


    fun insertExerciseDay(exerciseDays: ExerciseDay) {
        movesDao.insertExerciseDay(exerciseDays)
    }

    fun insertExerciseDayExercise(exerciseDayExercises: List<ExerciseDayExercise>) {
        movesDao.insertExerciseDayExercise(exerciseDayExercises)
    }

    fun insertSubExerciseDayExercise(exercise: List<ExerciseDayExercise>) {
        movesDao.insertSubExerciseDayExercise(exercise)
    }

    fun getSubExerciseDayExercises(titleName: String): List<ExerciseDayExercise> {
        return movesDao.getSubExerciseDayExercises(titleName)
    }

    fun getExerciseDayExercisesWithLevelOne(titleName: String): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelOne(titleName)
    }

    fun getExerciseDayExercisesWithLevelTwo(titleName: String): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelTwo(titleName)
    }

    fun getExerciseDayExercisesWithLevelThird(titleName: String): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelThird(titleName)
    }

    fun getExerciseListWithDayID(dayID: Int, level: Int,titleName: String): List<ExerciseDayExercise> {
        return movesDao.getExerciseListWithDayID(dayID, level,titleName)
    }

    fun getExerciseListByTitleName(titleName: String): List<ExerciseDayExercise> {
        return movesDao.getExerciseListByTitleName(titleName)
    }

    fun getExerciseListWithTitleAndLevel(
        titleName: String,
        level: Int
    ): List<ExerciseDayExercise> {
        return movesDao.getExerciseListWithTitleAndLEvel(titleName, level)
    }


    fun getExerciseByDay(dayId: Int): List<ExerciseDayWithExerciseDayExercise> {
        return movesDao.getExerciseDayWithExerciseDayExercise(dayId)
    }

    fun updateIsFavourite(exerciseId: Int){
        movesDao.updateIsFavourite(exerciseId)
    }

    fun updateIsFavouriteToFalse(exerciseId: Int){
        movesDao.updateIsFavouriteToFalse(exerciseId)
    }

    fun getIsFavouriteTrue():List<ExerciseDayExercise>{
        return movesDao.getIsFavouriteTrue()
    }

}