package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseDayJsonCompatibilityTest {
    @Test fun legacyStoredDayRetainsItsValuesAndFieldNames() {
        val legacy = """{"dayId":7,"day":7,"level":3,"exerciseCount":8,"exerciseTime":9,"exerciseKcal":42,"isCompleted":true,"homeId":1}"""
        val expected = ExerciseDay(7, 7, 3, 8, 9, 42, true, 1)
        val restored = ExerciseDay.fromJson(legacy)
        assertEquals(expected, restored)
        assertEquals(JsonParser.parseString(legacy), JsonParser.parseString(restored.toJson()))
    }
}
