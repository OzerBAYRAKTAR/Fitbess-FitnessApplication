package com.bayraktar.healthybackandneck

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.utils.ExerciseMedia
import org.json.JSONArray
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifDrawable
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ExerciseMediaTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun everyCatalogRowResolvesItsOriginalAnimatedGif() {
        val rowIds = mutableSetOf<Int>()
        val images = mutableSetOf<Int>()
        for (file in listOf("exercises.json", "subExercises.json")) {
            val catalog = JSONArray(context.assets.open(file).bufferedReader().use { it.readText() })
            for (index in 0 until catalog.length()) {
                val row = catalog.getJSONObject(index)
                val id = row.getInt("id")
                assertTrue("Duplicate row ID $id", rowIds.add(id))
                val name = row.optString("Image").ifEmpty { row.getString("image") }
                val drawable = ExerciseMedia.drawable(id)
                assertEquals("Catalog row $id", name, context.resources.getResourceEntryName(drawable))
                images.add(drawable)
            }
        }
        images.forEach { resource ->
            val gif = GifDrawable(context.resources, resource)
            try { assertTrue("Not animated: $resource", gif.numberOfFrames > 1) } finally { gif.recycle() }
        }
        assertEquals(R.drawable.placehlder, ExerciseMedia.drawable(-1))
    }

    @Test fun stalePersistedResourceIdCannotReplaceCrabWalkOrLoseFavorite() {
        val name = "media-regression-${UUID.randomUUID()}"
        val crab = WorkoutCatalogParser.readExercises(context, 1).first { it.dayId == 3 && it.exerciseName == "Crab Walk" }
        var db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
        try {
            // An older APK may have assigned this numeric ID to a food image or any
            // other drawable. It is valid in this APK, so NotFound handling is insufficient.
            db.moveDao().insertExerciseDayExercise(listOf(crab.copy(image = R.drawable.home_training_hero, isFavourite = true, isExerciseCompleted = true)))
            db.close()
            db = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            val stored = db.moveDao().getIsFavouriteTrue().single()
            assertEquals(R.drawable.home_training_hero, stored.image)
            assertEquals(R.drawable.e102_b13_crab_walk, ExerciseMedia.drawable(stored))
            assertTrue(stored.isFavourite)
            assertTrue(stored.isExerciseCompleted)
        } finally { db.close(); context.deleteDatabase(name) }
    }
}
