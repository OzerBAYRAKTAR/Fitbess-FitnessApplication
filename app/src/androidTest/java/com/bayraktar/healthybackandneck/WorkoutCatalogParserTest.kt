package com.bayraktar.healthybackandneck

import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCatalogParserTest {
    @Test fun bundledCatalogHasTwentyOneOrderedDaysForEveryLevelAndOnlyMatchingExercises() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dayAssets = listOf("firstday.json", "secondday.json", "thirdday.json")

        dayAssets.forEachIndexed { index, asset ->
            val days = WorkoutCatalogParser.readDays(context, asset)
            assertEquals(21, days.size)
            assertEquals((1..21).toList(), days.map { it.day })
            assertTrue(days.all { it.level == index + 1 })
        }

        (1..3).forEach { level ->
            val exercises = WorkoutCatalogParser.readExercises(context, level)
            assertFalse("Level $level must have bundled exercise data", exercises.isEmpty())
            assertTrue(exercises.all { it.level == level && it.titleName == "noTitle" })
        }
    }
}
