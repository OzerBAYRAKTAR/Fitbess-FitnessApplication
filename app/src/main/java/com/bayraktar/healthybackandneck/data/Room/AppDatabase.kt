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
    ExerciseDayExercise::class,SubExerciseDayExercise::class,CountModel::class,com.bayraktar.healthybackandneck.data.Models.WorkoutSession::class],version = 13, exportSchema = false)
@TypeConverters(MovesTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moveDao(): MovesDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        val MIGRATION_12_13 = object : androidx.room.migration.Migration(12, 13) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE exercise_days_new (dayId INTEGER NOT NULL, day INTEGER NOT NULL, level INTEGER NOT NULL, exerciseCount INTEGER NOT NULL, exerciseTime INTEGER NOT NULL, exerciseKcal INTEGER NOT NULL, isCompleted INTEGER NOT NULL, homeId INTEGER NOT NULL, PRIMARY KEY(level, dayId))")
                db.execSQL("INSERT INTO exercise_days_new SELECT dayId, day, level, exerciseCount, exerciseTime, exerciseKcal, isCompleted, homeId FROM exercise_days")
                db.execSQL("DROP TABLE exercise_days")
                db.execSQL("ALTER TABLE exercise_days_new RENAME TO exercise_days")
            }
        }

        val MIGRATION_11_12 = object : androidx.room.migration.Migration(11, 12) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS workout_sessions (id TEXT NOT NULL PRIMARY KEY, startedAt INTEGER NOT NULL, endedAt INTEGER NOT NULL, activeMillis INTEGER NOT NULL, completedMoves INTEGER NOT NULL, plannedMoves INTEGER NOT NULL, level INTEGER NOT NULL, day INTEGER NOT NULL, title TEXT NOT NULL)")
            }
        }
    }

}
