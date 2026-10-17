package com.bayraktar.healthybackandneck.data.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions ORDER BY endedAt DESC")
    fun observeSessions(): Flow<List<WorkoutSession>>
}
