package com.bayraktar.healthybackandneck.data.Repository

import android.content.Context
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.data.Room.MovesDao
import com.bayraktar.healthybackandneck.utils.LocaleHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/** Repository reads call this on their IO dispatcher, including the favorites catalog. */
@Singleton
class SubExerciseCatalog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: MovesDao,
) {
    private var seeded = false

    @Synchronized fun ensureSeeded() {
        if (seeded) return
        val localized = LocaleHelper.wrap(context)
        val source = context.assets.open("subExercises.json").bufferedReader().use { it.readText() }
        val array = JSONArray(source)
        val rows = List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val description = localized.resources.getIdentifier(item.getString("exerciseDescription"), "string", context.packageName)
            ExerciseDayExercise(
                id = item.getInt("id"), exerciseId = item.getInt("exerciseId"),
                titleName = item.getString("titleName"), level = item.getInt("level"), dayId = 0, step = 0,
                exerciseName = item.optString("exerciseName").ifBlank { item.getString("ExerciseName") },
                exerciseDescription = localized.getString(if (description != 0) description else R.string.not_found),
                image = context.resources.getIdentifier(item.getString("image"), "drawable", context.packageName),
                isExerciseCompleted = false,
            )
        }
        // Fill missing rows only: existing favorites, completions and unlock IDs stay intact.
        dao.insertMissingCatalogExercises(rows)
        seeded = true
    }
}
