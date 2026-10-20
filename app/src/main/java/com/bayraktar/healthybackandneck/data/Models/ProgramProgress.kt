package com.bayraktar.healthybackandneck.data.Models

/** Number of distinct plan days fully completed in a 21-day level. */
fun completedProgramDays(sessions: List<WorkoutSession>, level: Int): Int =
    sessions.asSequence()
        .filter { it.isComplete && it.level == level && it.day in 1..21 }
        .map { it.day }
        .distinct()
        .count()
