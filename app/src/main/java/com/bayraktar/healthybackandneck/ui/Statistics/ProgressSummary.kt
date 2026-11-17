package com.bayraktar.healthybackandneck.ui.Statistics

import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import java.util.Calendar
import java.util.TimeZone

enum class ProgressPeriod(val days: Int, val bucketDays: Int) { LAST_SEVEN(7, 1), LAST_FOUR_WEEKS(28, 7) }
data class ActivityBucket(val start: Long, val endExclusive: Long, val activeMillis: Long)
data class ProgressSummary(
    val start: Long,
    val end: Long,
    val completedWorkouts: Int,
    val activeMillis: Long,
    val completedMoves: Int,
    val recordedSessions: Int,
    val buckets: List<ActivityBucket>,
) {
    companion object {
        fun isTrustworthy(session: WorkoutSession, now: Long): Boolean =
            session.startedAt in 0..now && session.endedAt in session.startedAt..now &&
                session.activeMillis >= 0 && session.plannedMoves >= 0 && session.completedMoves in 0..session.plannedMoves

        fun trustworthy(sessions: List<WorkoutSession>, now: Long): List<WorkoutSession> = sessions.filter { isTrustworthy(it, now) }

        fun from(sessions: List<WorkoutSession>, now: Long, period: ProgressPeriod, zone: TimeZone = TimeZone.getDefault()): ProgressSummary {
            val start = Calendar.getInstance(zone).apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, -(period.days - 1))
            }
            val selected = trustworthy(sessions, now).filter { it.endedAt >= start.timeInMillis }
            val buckets = List(period.days / period.bucketDays) { index ->
                val day = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, index * period.bucketDays) }
                val next = (day.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, period.bucketDays) }
                ActivityBucket(day.timeInMillis, next.timeInMillis,
                    selected.filter { it.endedAt in day.timeInMillis until next.timeInMillis }.sumOf { it.activeMillis.coerceAtLeast(0) })
            }
            return ProgressSummary(start.timeInMillis, now, selected.count { it.isComplete },
                selected.sumOf { it.activeMillis.coerceAtLeast(0) }, selected.sumOf { it.completedMoves.coerceAtLeast(0) }, selected.size, buckets)
        }
    }
}
