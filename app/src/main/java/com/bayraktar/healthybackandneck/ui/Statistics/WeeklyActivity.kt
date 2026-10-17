package com.bayraktar.healthybackandneck.ui.Statistics

import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import java.util.Calendar
import java.util.TimeZone

data class ActivityDay(val start: Long, val activeMillis: Long)

object WeeklyActivity {
    fun days(sessions: List<WorkoutSession>, now: Long, zone: TimeZone = TimeZone.getDefault()): List<ActivityDay> {
        val start = Calendar.getInstance(zone).apply {
            timeInMillis = now
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return List(7) { offset ->
            val day = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) }
            val next = (day.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            ActivityDay(day.timeInMillis, sessions.filter { it.endedAt in day.timeInMillis until next.timeInMillis && it.endedAt <= now }.sumOf { it.activeMillis.coerceAtLeast(0) })
        }
    }
}
