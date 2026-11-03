package com.bayraktar.healthybackandneck

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutExerciseCountTest {
    @Test fun assetMetadataRefreshUpdatesCaloriesWithoutResettingCompletedDays() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = WorkoutCatalogParser.readDays(context, "thirdday.json")
        val stored = catalog.map { day ->
            day.copy(exerciseKcal = day.exerciseKcal * 3, isCompleted = day.day == 6)
        }

        val refreshed = WorkoutCatalogParser.mergeDayMetadata(catalog, stored)

        assertEquals(catalog.map { it.exerciseKcal }, refreshed.map { it.exerciseKcal })
        assertEquals(stored.map { it.isCompleted }, refreshed.map { it.isCompleted })
    }

    @Test fun dayCardCountsMatchTheCatalogRowsShownInEachDayForAllLevels() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val daysByLevel = listOf("firstday.json", "secondday.json", "thirdday.json")
                .map { WorkoutCatalogParser.readDays(context, it) }

            (1..3).forEach { level ->
                val exercises = WorkoutCatalogParser.readExercises(context, level)
                database.moveDao().insertExerciseDayExercise(exercises)
                val actualCounts = database.moveDao().getExerciseCountsByDay(level, "noTitle")
                    .associate { it.dayId to it.exerciseCount }
                val expectedCounts = exercises.groupingBy { it.dayId }.eachCount()

                assertEquals("level $level day counts", expectedCounts, actualCounts)
                daysByLevel[level - 1].forEach { day ->
                    assertEquals("level $level day ${day.day}", day.exerciseCount, actualCounts[day.dayId])
                }
            }
        } finally {
            database.close()
        }
    }
}
