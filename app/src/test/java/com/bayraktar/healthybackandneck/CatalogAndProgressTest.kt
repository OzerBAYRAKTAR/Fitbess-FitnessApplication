package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Models.completedProgramDays
import com.bayraktar.healthybackandneck.ui.Statistics.WeeklyActivity
import com.bayraktar.healthybackandneck.utils.MoveAccess
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CatalogAndProgressTest {
    private fun move(id: Int = 512, category: String = "stretch", level: Int = 2, day: Int = 0) =
        ExerciseDayExercise(id, 42, 1, category, day, "Move", "Description", 0, false, level)

    @Test fun firstGroupOfEveryBodyRegionIsFreeAndLaterModelsNeedTheirOwnReward() {
        listOf("stretch", "bellyneck", "arm", "abs", "legbutt").forEach { category ->
            assertTrue(MoveAccess.allowed(move(category = category, level = 1), emptySet()))
            assertFalse(MoveAccess.allowed(move(category = category), emptySet()))
            assertTrue(MoveAccess.allowed(move(category = category), setOf(512)))
            assertFalse(MoveAccess.allowed(move(id = 513, category = category), setOf(512)))
        }
        assertTrue(MoveAccess.allowed(move(category = "noTitle", day = 21), emptySet()))
    }

    @Test fun oneLevelRewardUnlocksOnlyTheSelectedCategoryAndLevel() {
        val selectedLevel = listOf(
            move(id = 1, category = "arm", level = 2),
            move(id = 2, category = "arm", level = 2),
            move(id = 3, category = "arm", level = 3),
            move(id = 4, category = "abs", level = 2),
        )

        assertEquals(setOf(1, 2), MoveAccess.levelUnlockIds(selectedLevel))
        assertEquals(setOf(1), MoveAccess.singleUnlockIds(selectedLevel[0]))
        assertEquals(setOf(2), MoveAccess.singleUnlockIds(selectedLevel[1]))
        assertTrue(MoveAccess.singleUnlockIds(move(level = 1)).isEmpty())
        assertTrue(MoveAccess.allowed(selectedLevel[0], MoveAccess.levelUnlockIds(selectedLevel)))
        assertFalse(MoveAccess.allowed(selectedLevel[2], MoveAccess.levelUnlockIds(selectedLevel)))
        assertFalse(MoveAccess.allowed(selectedLevel[3], MoveAccess.levelUnlockIds(selectedLevel)))
    }

    @Test fun chartUsesLocalDayBoundariesIncludesPartialEffortAndExcludesFuture() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.UK)
            val zone = TimeZone.getTimeZone("Europe/Istanbul")
            fun at(day: Int, hour: Int, minute: Int) = Calendar.getInstance(zone).apply {
                clear(); set(2026, Calendar.OCTOBER, day, hour, minute)
            }.timeInMillis
            fun session(end: Long, active: Long, complete: Int = 1) = WorkoutSession(end.toString(), end - active, end, active, complete, 2, 1, 1, "Test")
            val days = WeeklyActivity.days(listOf(
                session(at(12, 23, 59), 30000), session(at(13, 0, 0), 60000, 2),
                session(at(16, 13, 0), 90000), session(at(11, 23, 59), 999999)
            ), at(16, 12, 0), zone)
            assertEquals(7, days.size)
            assertEquals(30000, days[0].activeMillis)
            assertEquals(60000, days[1].activeMillis)
            assertEquals(0, days[4].activeMillis)
            assertEquals(90000, days.sumOf { it.activeMillis })
        } finally { Locale.setDefault(previous) }
    }

    @Test fun programProgressCountsOnlyDistinctFullyCompletedDaysUsingTheirPlannedMoveCount() {
        fun session(id: String, done: Int, planned: Int, level: Int = 1, day: Int = 1) =
            WorkoutSession(id, 1, 2, 1200, done, planned, level, day, "Test")

        val sessions = listOf(
            session("partial", 7, 8),
            session("complete-eight-moves", 8, 8),
            session("repeat-day", 8, 8),
            session("other-level", 8, 8, level = 2),
            session("outside-program", 8, 8, day = 22),
        )

        assertEquals(1, completedProgramDays(sessions, level = 1))
        assertEquals(1, completedProgramDays(sessions, level = 2))
        assertEquals(0, completedProgramDays(emptyList(), level = 3))
    }
}
