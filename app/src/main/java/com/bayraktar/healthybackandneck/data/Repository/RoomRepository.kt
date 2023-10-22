package com.bayraktar.healthybackandneck.data.Repository

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

    fun getExerciseByDay(dayId: Int): List<ExerciseDayWithExerciseDayExercise> {
        return movesDao.getExerciseDayWithExerciseDayExercise(dayId)
    }

}