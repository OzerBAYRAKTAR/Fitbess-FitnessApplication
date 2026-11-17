package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Models.completedProgramDays
import com.bayraktar.healthybackandneck.ui.Statistics.ProgressPeriod
import com.bayraktar.healthybackandneck.ui.Statistics.ProgressSummary
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class ProgressSummaryTest {
    private val zone = TimeZone.getTimeZone("Europe/Istanbul")
    private fun at(day: Int, hour: Int = 12) = Calendar.getInstance(zone).apply { clear(); set(2026, Calendar.NOVEMBER, day, hour, 0) }.timeInMillis
    private fun session(end: Long, active: Long = 60000, done: Int = 2, planned: Int = 2) =
        WorkoutSession("$end-$done", end - 120000, end, active, done, planned, 1, 1, "Test")

    @Test fun sevenDaysIncludePartialEffortAndBoundaryButNotOlderOrFutureRecords() {
        val now = at(17)
        val source = listOf(session(at(11, 0), 30000, 1), session(at(17), 90000), session(at(10, 23)), session(now + 1))
        val result = ProgressSummary.from(source, now, ProgressPeriod.LAST_SEVEN, zone)
        assertEquals(at(11, 0), result.start)
        assertEquals(7, result.buckets.size)
        assertEquals(1, result.completedWorkouts)
        assertEquals(120000, result.activeMillis)
        assertEquals(3, result.completedMoves)
        assertEquals(30000, result.buckets.first().activeMillis)
        assertEquals(90000, result.buckets.last().activeMillis)
    }

    @Test fun fourWeeksUseFourConsecutiveSevenDayBucketsWithNoDoubleCounting() {
        val now = at(28)
        val source = listOf(session(at(1, 0)), session(at(7, 23)), session(at(8, 0)), session(at(14)), session(at(21)), session(now))
        val result = ProgressSummary.from(source, now, ProgressPeriod.LAST_FOUR_WEEKS, zone)
        assertEquals(at(1, 0), result.start)
        assertEquals(listOf(120000L, 120000L, 60000L, 60000L), result.buckets.map { it.activeMillis })
        assertEquals(result.activeMillis, result.buckets.sumOf { it.activeMillis })
        assertEquals(6, result.completedWorkouts) // Repeated program days are still actual workout sessions.
    }

    @Test fun corruptCountsTimesAndNegativeEffortNeverInflateTheDashboard() {
        val valid = session(at(17), 1000, 1)
        val invalid = listOf(valid.copy(activeMillis = -1), valid.copy(completedMoves = -1),
            valid.copy(completedMoves = 4), valid.copy(plannedMoves = -1),
            valid.copy(startedAt = at(18)), valid.copy(endedAt = valid.startedAt - 1))
        val result = ProgressSummary.from(invalid + valid, at(17), ProgressPeriod.LAST_SEVEN, zone)
        assertEquals(1000, result.activeMillis)
        assertEquals(1, result.completedMoves)
        assertEquals(0, result.completedWorkouts)
    }

    @Test fun emptyRecordsHaveRealZeroBucketsAndNoInventedTargetOrScore() {
        val result = ProgressSummary.from(emptyList(), at(17), ProgressPeriod.LAST_SEVEN, zone)
        assertEquals(0, result.completedWorkouts)
        assertEquals(0, result.activeMillis)
        assertEquals(7, result.buckets.size)
        assertTrue(result.buckets.all { it.activeMillis == 0L })
        assertEquals(0, result.recordedSessions)
        val realZero = ProgressSummary.from(listOf(session(at(17), active = 0)), at(17), ProgressPeriod.LAST_SEVEN, zone)
        assertEquals(1, realZero.recordedSessions)
        assertEquals(0, realZero.activeMillis)
    }

    @Test fun sharedAllHistoryFilterKeepsProgramAndHistoryConsistentWithoutPeriodTruncation() {
        val validOld = session(at(1)).copy(day = 1)
        val validPartial = session(at(17), done = 1).copy(day = 2)
        val future = session(at(18)).copy(day = 3)
        val corrupt = session(at(16)).copy(activeMillis = -1, day = 4)
        val trusted = ProgressSummary.trustworthy(listOf(validOld, validPartial, future, corrupt), at(17))
        assertEquals(listOf(validOld, validPartial), trusted)
        assertEquals(1, completedProgramDays(trusted, 1))
        assertEquals(1, ProgressSummary.from(trusted, at(17), ProgressPeriod.LAST_SEVEN, zone).recordedSessions)
    }

    @Test fun localDayBucketsFollowDaylightSavingRatherThanFixedTwentyFourHours() {
        val berlin = TimeZone.getTimeZone("Europe/Berlin")
        val now = Calendar.getInstance(berlin).apply { clear(); set(2026, Calendar.MARCH, 30, 12, 0) }.timeInMillis
        val result = ProgressSummary.from(emptyList(), now, ProgressPeriod.LAST_SEVEN, berlin)
        assertTrue(result.buckets.any { it.endExclusive - it.start == 23 * 3600000L })
        assertTrue(result.buckets.zipWithNext().all { (a, b) -> a.endExclusive == b.start })
    }
}
