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

    fun getExercisesWithLevelOne(): Int {
        return movesDao.getExerciseCountForLevelOne()
    }

    fun getExerciseDayExercisesWithLevelOne(): List<ExerciseDayExercise> {
        return movesDao.getExerciseDayExercisesWithLevelOne()
    }


    fun getExerciseByDay(dayId: Int): List<ExerciseDayWithExerciseDayExercise> {
        return movesDao.getExerciseDayWithExerciseDayExercise(dayId)
    }

}