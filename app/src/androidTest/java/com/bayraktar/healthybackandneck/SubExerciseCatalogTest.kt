package com.bayraktar.healthybackandneck

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.SubExerciseCatalog
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.ui.HomePage.TrainingCatalog
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SubExerciseCatalogTest {
    @Test fun seedingIncludesEveryRegionAndPreservesExistingFavoriteAndCompletion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val dao = database.moveDao()
            SubExerciseCatalog(context, dao).ensureSeeded()
            assertEquals(mapOf("arm" to 3, "bellyneck" to 4, "abs" to 3, "legbutt" to 4, "stretch" to 3), TrainingCatalog.regions.associate { it.workouts.first().category to it.workouts.size })
            TrainingCatalog.regions.flatMap { it.workouts }.forEach { workout ->
                val moves = dao.getExerciseListWithTitleAndLEvel(workout.category, workout.level)
                assertTrue("Missing ${workout.category} / ${workout.level}", moves.isNotEmpty())
                assertTrue(moves.all { it.image != 0 })
                assertTrue(moves.all { com.bayraktar.healthybackandneck.utils.MoveAccess.allowed(it, emptySet()) == (workout.level == 1) })
                val earned = moves.map { it.id }.toSet()
                SubExerciseCatalog(context, dao).ensureSeeded()
                assertTrue(dao.getExerciseListWithTitleAndLEvel(workout.category, workout.level).all { com.bayraktar.healthybackandneck.utils.MoveAccess.allowed(it, earned) })
            }
            val before = dao.getExerciseListWithTitleAndLEvel("arm", 1)
            val favorite = before.first().copy(isFavourite = true, isExerciseCompleted = true)
            dao.insertSubExerciseDayExercise(listOf(favorite))
            SubExerciseCatalog(context, dao).ensureSeeded()
            val after = dao.getExerciseListWithTitleAndLEvel("arm", 1)
            assertEquals(before.size, after.size)
            assertEquals(favorite, after.first { it.id == favorite.id })
        } finally { database.close() }
    }
}
