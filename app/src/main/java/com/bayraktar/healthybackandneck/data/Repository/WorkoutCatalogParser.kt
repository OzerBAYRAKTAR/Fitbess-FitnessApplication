package com.bayraktar.healthybackandneck.data.Repository

import android.annotation.SuppressLint
import android.content.Context
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import org.json.JSONArray

/** Parses the bundled workout catalog. Call from a background dispatcher. */
object WorkoutCatalogParser {
    fun readDays(context: Context, assetName: String): List<ExerciseDay> {
        val json = context.assets.open(assetName).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val array = JSONArray(json)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            ExerciseDay(
                dayId = item.optInt("dayId"),
                level = item.optInt("level"),
                isCompleted = item.optBoolean("isCompleted"),
                exerciseCount = item.optInt("exerciseCount"),
                day = item.optInt("day"),
                exerciseTime = item.optInt("exerciseTime"),
                exerciseKcal = item.optInt("exerciseKcal"),
                homeId = item.optInt("homeId"),
            )
        }.sortedBy(ExerciseDay::day)
    }

    /** Refresh bundled day metadata while keeping user completion state stored in Room. */
    fun mergeDayMetadata(catalogDays: List<ExerciseDay>, storedDays: List<ExerciseDay>): List<ExerciseDay> {
        val storedByLevelAndDay = storedDays.associateBy { it.level to it.dayId }
        return catalogDays.map { catalogDay ->
            val storedDay = storedByLevelAndDay[catalogDay.level to catalogDay.dayId]
            catalogDay.copy(isCompleted = storedDay?.isCompleted ?: catalogDay.isCompleted)
        }
    }

    @SuppressLint("DiscouragedApi")
    fun readExercises(context: Context, level: Int): List<ExerciseDayExercise> {
        val json = context.assets.open("exercises.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val array = JSONArray(json)
        val resources = context.resources
        val packageName = context.packageName
        val notFound = context.getString(R.string.not_found)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                if (item.optInt("Level") != level) continue

                val descriptionId = resources.getIdentifier(item.optString("ExerciseDescription"), "string", packageName)
                add(
                    ExerciseDayExercise(
                        id = item.optInt("id"),
                        dayId = item.optInt("DayId"),
                        step = item.optInt("Step"),
                        exerciseName = item.optString("ExerciseName"),
                        exerciseDescription = if (descriptionId != 0) context.getString(descriptionId) else notFound,
                        image = resources.getIdentifier(item.optString("Image"), "drawable", packageName),
                        isExerciseCompleted = item.optBoolean("IsExerciseCompleted"),
                        exerciseId = item.optInt("ExerciseId"),
                        level = item.optInt("Level"),
                        titleName = "noTitle",
                    ),
                )
            }
        }
    }
}
