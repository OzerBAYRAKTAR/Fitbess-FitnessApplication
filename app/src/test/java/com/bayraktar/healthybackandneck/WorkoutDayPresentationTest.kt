package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.utils.continueDayPosition
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutDayPresentationTest {
    private val days = (1..7).map { ExerciseDay(it, it, 2, 8, 7, 80, it <= 3, 2) }

    @Test fun resumesOnlyAccessibleIncompleteDay() {
        assertEquals(1, continueDayPosition(days, setOf(1), emptySet(), false, 2))
        assertEquals(1, continueDayPosition(days, setOf(1), emptySet(), false, 7))
        assertEquals(3, continueDayPosition(days, setOf(1, 2, 3), setOf("2:4"), false, 4))
        assertEquals(2, continueDayPosition(days, setOf(1, 2), setOf("1:4"), false, 4))
    }

    @Test fun adFreeAndCompletedFallbackDoNotCreateAccess() {
        assertEquals(6, continueDayPosition(days, emptySet(), emptySet(), true, 7))
        assertEquals(2, continueDayPosition(days, setOf(1, 2, 3), emptySet(), false, null))
        assertEquals(-1, continueDayPosition(emptyList(), emptySet(), emptySet(), false, null))
    }
}
