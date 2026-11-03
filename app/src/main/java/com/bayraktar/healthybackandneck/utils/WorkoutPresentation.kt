package com.bayraktar.healthybackandneck.utils

import android.content.Context
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDay

fun workoutSessionTitle(context: Context, day: ExerciseDay?): String {
    if (day == null) return context.getString(R.string.exercice)
    val level = context.getString(when(day.level) { 1 -> R.string.easy_level; 2 -> R.string.middle_level; else -> R.string.hard_level }).replace("\n", " ")
    return "$level · ${context.getString(R.string.label_day)} ${day.day}"
}
fun workoutClock(millis: Long): String {
    val seconds = ((millis.coerceAtLeast(0) + 999) / 1000).toInt()
    return String.format(java.util.Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60)
}
