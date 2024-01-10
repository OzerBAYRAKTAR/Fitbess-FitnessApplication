package com.bayraktar.healthybackandneck.data.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bayraktar.healthybackandneck.data.Models.CountModel
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.HomeItem
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.SubExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.MotivationNotificationState
import com.bayraktar.healthybackandneck.data.Models.WaterReminderState
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [MotivationNotificationState::class,WaterReminderState::class,ExerciseDay::class,
    ExerciseDayExercise::class,SubExerciseDayExercise::class,CountModel::class],version = 11, exportSchema = false)
@TypeConverters(MovesTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moveDao(): MovesDao

}