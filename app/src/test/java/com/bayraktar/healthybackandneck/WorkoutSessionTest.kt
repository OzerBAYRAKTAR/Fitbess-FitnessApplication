package com.bayraktar.healthybackandneck
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import org.junit.Assert.*
import org.junit.Test
class WorkoutSessionTest {
    private fun session(done: Int, planned: Int) = WorkoutSession("one", 1, 2, 1200, done, planned, 1, 1, "Test")
    @Test fun skippingDoesNotCompleteAProgramDay() { assertFalse(session(0, 7).isComplete); assertEquals(7, session(0, 7).skippedMoves) }
    @Test fun partialAndEmptyPlansAreNotComplete() { assertFalse(session(6, 7).isComplete); assertFalse(session(0, 0).isComplete) }
    @Test fun allPlannedMovesCompleteTheSession() { assertTrue(session(7, 7).isComplete); assertEquals(0, session(7, 7).skippedMoves) }
}
