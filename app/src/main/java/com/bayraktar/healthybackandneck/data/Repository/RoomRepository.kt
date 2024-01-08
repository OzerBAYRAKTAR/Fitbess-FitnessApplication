package com.bayraktar.healthybackandneck.data.Repository

import androidx.lifecycle.LiveData
import com.bayraktar.healthybackandneck.data.Models.CountModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.Relations.ExerciseDayWithExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Room.MovesDao
import javax.inject.Inject


class RoomRepository @Inject constructor(private val movesDao: MovesDao) {


    fun insertExerciseDay(exerciseDays: ExerciseDay) {
        movesDao.insertExerciseDay(exerciseDays)
    }

    fun insertCountTable(id: CountModel) {
        movesDao.insertCounTable(id)
    }

    fun insertExerciseDayExercise(exerciseDayExercises: List<ExerciseDayExercise>) {
        movesDao.insertExerciseDayExercise(exerciseDayExercises)
    }

    fun insertExerciseDaysList(exerciseDay: List<ExerciseDay>) {
        movesDao.insertExerciseDaysList(exerciseDay)
    }

    fun getExerciseDaysLevel1(): List<ExerciseDay> {
        return movesDao.getExerciseDaysLevel1()
    }

    fun getCount(id: Int): CountModel {
        return movesDao.getCount(id)
    }


    fun getExerciseDaysLevel2(): List<ExerciseDay> {
        return movesDao.getExerciseDaysLevel2()
    }

    fun getExerciseDaysLevel3(): List<ExerciseDay> {
        return movesDao.getExerciseDaysLevel3()
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


    fun updateIsFavourite(exerciseId: Int){
        movesDao.updateIsFavourite(exerciseId)
    }

    fun updateIsFavouriteToFalse(exerciseId: Int){
        movesDao.updateIsFavouriteToFalse(exerciseId)
    }

    fun updateCount(): Int{
       return movesDao.updateCount()
    }

    fun getIsFavouriteTrue():List<ExerciseDayExercise>{
        return movesDao.getIsFavouriteTrue()
    }

    fun updateIsCompletedTrue(level: Int,day: Int){
        movesDao.updateIsCompletedTrue(level,day)
    }

}