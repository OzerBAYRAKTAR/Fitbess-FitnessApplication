package com.bayraktar.healthybackandneck

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession
import com.bayraktar.healthybackandneck.data.Repository.WorkoutRepository
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import com.bayraktar.healthybackandneck.ui.ExerciseMoves.WorkoutSessionViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class WorkoutPersistenceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun migrationKeepsFavoritesAndSessionInsertIsIdempotent() = runBlocking {
        val name = "migration-${UUID.randomUUID()}"
        var database = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
        try {
            val favorite = ExerciseDayExercise(-1, -1, 1, "Test", 1, "Synthetic", "Test", 0, false, 1, isFavourite = true)
            database.moveDao().insertExerciseDayExercise(listOf(favorite))
            database.close()
            // The v11 schema is exactly the unchanged v12 tables without workout_sessions.
            SQLiteDatabase.openDatabase(context.getDatabasePath(name).path, null, SQLiteDatabase.OPEN_READWRITE).use {
                it.execSQL("DROP TABLE workout_sessions")
                it.version = 11
            }
            database = Room.databaseBuilder(context, AppDatabase::class.java, name)
                .addMigrations(AppDatabase.MIGRATION_11_12).build()
            assertEquals("Synthetic", database.moveDao().getIsFavouriteTrue().single().exerciseName)
            val session = WorkoutSession("unique", 10, 20, 500, 1, 2, 1, 1, "Test")
            database.workoutDao().insert(session)
            database.workoutDao().insert(session)
            assertEquals(listOf(session), database.workoutDao().observeSessions().first())
            database.close()
            database = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
            assertEquals(session, database.workoutDao().observeSessions().first().single())
        } finally {
            database.close()
            context.deleteDatabase(name) // Only this test's uniquely named database.
        }
    }

    @Test fun pausesAndRecreationExcludeDowntimeAndKeepUniqueCompletedMoves() = runBlocking {
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val state = SavedStateHandle()
            val repository = WorkoutRepository(database)
            var model = WorkoutSessionViewModel(state, repository)
            instrumentation.runOnMainSync { model.begin(null, 2, "Test"); model.resume() }
            SystemClock.sleep(120)
            instrumentation.runOnMainSync { model.pause(); model.completeMove(0); model.completeMove(0) }
            val activeBefore = state.get<Bundle>("workout")!!.getLong("active")
            SystemClock.sleep(250)
            // Reconstruct from persisted state, as after process recreation, with no running timestamp.
            instrumentation.runOnMainSync {
                model = WorkoutSessionViewModel(SavedStateHandle(mapOf("workout" to Bundle(state.get<Bundle>("workout")!!))), repository)
                model.finish()
            }
            val saved = withTimeout(15000) { model.saveState.first { it is WorkoutSessionViewModel.SaveState.Saved || it is WorkoutSessionViewModel.SaveState.Failed } } as WorkoutSessionViewModel.SaveState.Saved
            assertEquals(activeBefore, saved.session.activeMillis)
            assertTrue(activeBefore >= 100)
            assertEquals(1, saved.session.completedMoves)
            assertFalse(saved.session.isComplete)
            instrumentation.runOnMainSync { model.finish() }
            withTimeout(15000) { model.saveState.first { it is WorkoutSessionViewModel.SaveState.Saved || it is WorkoutSessionViewModel.SaveState.Failed } }
            assertEquals(1, database.workoutDao().observeSessions().first().size)
        } finally { database.close() }
    }
}
