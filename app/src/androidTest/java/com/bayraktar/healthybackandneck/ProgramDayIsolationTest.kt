package com.bayraktar.healthybackandneck

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bayraktar.healthybackandneck.data.Repository.WorkoutCatalogParser
import com.bayraktar.healthybackandneck.data.Room.AppDatabase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProgramDayIsolationTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val assets = listOf("firstday.json", "secondday.json", "thirdday.json")
    private val legacyTable = "CREATE TABLE exercise_days (dayId INTEGER NOT NULL PRIMARY KEY, day INTEGER NOT NULL, level INTEGER NOT NULL, exerciseCount INTEGER NOT NULL, exerciseTime INTEGER NOT NULL, exerciseKcal INTEGER NOT NULL, isCompleted INTEGER NOT NULL, homeId INTEGER NOT NULL)"

    @Test fun legacySchemaReproducesCrossLevelReplacement() {
        SQLiteDatabase.create(null).use { db ->
            db.execSQL(legacyTable)
            db.execSQL("INSERT INTO exercise_days VALUES (6,6,3,8,7,80,1,3)")
            db.execSQL("INSERT OR REPLACE INTO exercise_days VALUES (6,6,1,8,7,80,0,1)")
            db.rawQuery("SELECT COUNT(*) FROM exercise_days WHERE level=3 AND dayId=6", null).use {
                assertTrue(it.moveToFirst())
                assertEquals("The old key deletes advanced day 6", 0, it.getInt(0))
            }
        }
    }

    @Test fun allLevelsAndEarnedAccessSurviveRefreshAndReopen() {
        val name = "day-isolation-${UUID.randomUUID()}"
        try {
            open(name).useDatabase { db ->
                val dao = db.moveDao()
                assets.forEach { dao.insertExerciseDaysList(WorkoutCatalogParser.readDays(context, it)) }
                dao.updateIsCompletedTrue(3, 6)
                dao.updateIsCompletedTrue(3, 7)
                // Simulate switching levels and a stale catalog refresh after earning access.
                repeat(3) {
                    assets.reversed().forEach { dao.insertExerciseDaysList(WorkoutCatalogParser.readDays(context, it)) }
                }
                assertDays(db)
                val stale = WorkoutCatalogParser.readDays(context, "thirdday.json").first { it.day == 6 }
                dao.insertExerciseDay(stale.copy(exerciseKcal = 42, isCompleted = false))
                assertTrue(dao.getExerciseDaysLevel3().first { it.day == 6 }.isCompleted)
                assertEquals(42, dao.getExerciseDaysLevel3().first { it.day == 6 }.exerciseKcal)
            }
            open(name).useDatabase { assertDays(it) }
        } finally { context.deleteDatabase(name) }
    }

    @Test fun version12MigrationPreservesRowsAndOtherTables() {
        val name = "day-migration-${UUID.randomUUID()}"
        try {
            // Other tables are unchanged in v12. Replace only the changed table with its
            // exact old DDL to exercise Room's real onUpgrade and schema validation.
            open(name).useDatabase { db ->
                db.openHelper.writableDatabase.execSQL("INSERT INTO workout_sessions VALUES ('migration-sentinel',1,2,1000,8,8,3,5,'Advanced')")
            }
            SQLiteDatabase.openDatabase(context.getDatabasePath(name).path, null, SQLiteDatabase.OPEN_READWRITE).use { db ->
                db.execSQL("DROP TABLE exercise_days")
                db.execSQL(legacyTable)
                db.execSQL("INSERT INTO exercise_days VALUES (6,6,3,8,7,80,1,3)")
                db.execSQL("INSERT INTO exercise_days VALUES (7,7,3,8,7,80,1,3)")
                db.execSQL("DROP TABLE room_master_table")
                db.version = 12
            }
            open(name).useDatabase { db ->
                assertEquals(2, db.moveDao().getExerciseDaysLevel3().size)
                assertTrue(db.moveDao().getExerciseDaysLevel3().all { it.isCompleted })
                assertEquals(80, db.moveDao().getExerciseDaysLevel3().first().exerciseKcal)
                assets.forEach { db.moveDao().insertExerciseDaysList(WorkoutCatalogParser.readDays(context, it)) }
                assertDays(db)
                db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM workout_sessions WHERE id='migration-sentinel'").use {
                    assertTrue(it.moveToFirst())
                    assertEquals(1, it.getInt(0))
                }
            }
            open(name).useDatabase { assertDays(it) }
        } finally { context.deleteDatabase(name) }
    }

    private fun open(name: String) = Room.databaseBuilder(context, AppDatabase::class.java, name)
        .addMigrations(AppDatabase.MIGRATION_11_12, AppDatabase.MIGRATION_12_13).build()

    private fun AppDatabase.useDatabase(block: (AppDatabase) -> Unit) {
        try { block(this) } finally { close() }
    }

    private fun assertDays(db: AppDatabase) {
        val dao = db.moveDao()
        val levels = listOf(dao.getExerciseDaysLevel1(), dao.getExerciseDaysLevel2(), dao.getExerciseDaysLevel3())
        levels.forEachIndexed { index, days ->
            assertEquals(21, days.size)
            assertEquals((1..21).toList(), days.map { it.day })
            assertTrue(days.all { it.level == index + 1 })
        }
        assertTrue(levels[2].first { it.day == 6 }.isCompleted)
        assertTrue(levels[2].first { it.day == 7 }.isCompleted)
        assertFalse(levels[0].first { it.day == 6 }.isCompleted)
        assertFalse(levels[1].first { it.day == 6 }.isCompleted)
    }
}
