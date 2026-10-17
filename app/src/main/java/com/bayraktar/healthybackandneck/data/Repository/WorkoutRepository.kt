package com.bayraktar.healthybackandneck.data.Repository

import androidx.room.withTransaction
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(private val database: AppDatabase) {
    val sessions = database.workoutDao().observeSessions()
    suspend fun save(session: WorkoutSession) = database.withTransaction {
        database.workoutDao().insert(session)
        if (session.isComplete && session.level in 1..3 && session.day in 1..20) {
            database.moveDao().updateIsCompletedTrue(session.level, session.day + 1)
        }
    }
}
