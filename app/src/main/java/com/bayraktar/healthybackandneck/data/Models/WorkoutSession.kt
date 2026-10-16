package com.bayraktar.healthybackandneck.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey val id: String,
    val startedAt: Long,
    val endedAt: Long,
    val activeMillis: Long,
    val completedMoves: Int,
    val plannedMoves: Int,
    val level: Int,
    val day: Int,
    val title: String,
) {
    val skippedMoves: Int get() = plannedMoves - completedMoves
    val isComplete: Boolean get() = plannedMoves > 0 && completedMoves == plannedMoves
}
