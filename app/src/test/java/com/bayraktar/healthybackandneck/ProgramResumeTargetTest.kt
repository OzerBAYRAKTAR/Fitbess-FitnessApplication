package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.ui.HomePage.ProgramResumeTarget
import com.bayraktar.healthybackandneck.ui.HomePage.programResumeTarget
import org.junit.Assert.*
import org.junit.Test

class ProgramResumeTargetTest {
    private fun session(level: Int = 2, day: Int = 4, completed: Int = 7, planned: Int = 7, ended: Long = 10) =
        WorkoutSession("$level-$day-$ended", 0, ended, 1000, completed, planned, level, day, "Program")

    @Test fun emptyHistoryDoesNotInventProgress() { assertNull(programResumeTarget(emptyList())) }
    @Test fun partialSessionReturnsToSameDay() {
        assertEquals(ProgramResumeTarget(2, 4), programResumeTarget(listOf(session(completed = 6))))
    }
    @Test fun allMovesCompletedAdvancesOneDay() {
        assertEquals(ProgramResumeTarget(2, 5), programResumeTarget(listOf(session())))
    }
    @Test fun emptySessionIsNotCompletion() {
        assertEquals(ProgramResumeTarget(2, 4), programResumeTarget(listOf(session(completed = 0, planned = 0))))
    }
    @Test fun lastDayNeverProducesInvalidDay22() {
        assertEquals(ProgramResumeTarget(3, 21), programResumeTarget(listOf(session(level = 3, day = 21))))
    }
    @Test fun laterRegionSessionDoesNotReplaceProgramTarget() {
        assertEquals(ProgramResumeTarget(1, 3), programResumeTarget(listOf(
            session(level = 1, day = 2), session(day = 0, ended = 30))))
    }
    @Test fun latestProgramWinsRegardlessOfInputOrder() {
        assertEquals(ProgramResumeTarget(3, 7), programResumeTarget(listOf(
            session(level = 3, day = 7, completed = 2, ended = 30), session(ended = 20))))
    }
}
