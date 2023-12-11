package com.bayraktar.healthybackandneck.data.Repository

import androidx.lifecycle.LiveData
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Room.MovesDao
import javax.inject.Inject


class RoomRepository @Inject constructor(private val movesDao: MovesDao) {


    fun insertExerciseDay(exerciseDays: ExerciseDay) {
        movesDao.insertExerciseDay(exerciseDays)
    }

    fun insertExerciseDayExercise(exerciseDayExercises: List<ExerciseDayExercise>) {
        movesDao.insertExerciseDayExercise(exerciseDayExercises)
    }

    fun insertSubExerciseDayExercise(subExercise: List<SubExerciseDayExercise>) {
        movesDao.insertSubExerciseDayExercise(subExercise)
    }

    fun getSubExerciseDayExercises(): List<SubExerciseDayExercise> {
        return movesDao.getSubExerciseDayExercises()
    }

    fun getExerciseDayExercisesWithLevelOne(): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelOne()
    }

    fun getExerciseDayExercisesWithLevelTwo(): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelTwo()
    }

    fun getExerciseDayExercisesWithLevelThird(): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelThird()
    }

    fun getExerciseListWithDayID(dayID: Int, level: Int): List<ExerciseDayExercise> {
        return movesDao.getExerciseListWithDayID(dayID, level)
    }

    fun getExerciseListByTitleName(titleName: String): List<SubExerciseDayExercise> {
        return movesDao.getExerciseListByTitleName(titleName)
    }

    fun getExerciseListWithTitleAndLevel(
        titleName: String,
        level: Int
    ): List<SubExerciseDayExercise> {
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

}