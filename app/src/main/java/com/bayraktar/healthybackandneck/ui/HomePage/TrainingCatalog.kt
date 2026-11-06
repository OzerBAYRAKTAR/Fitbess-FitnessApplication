package com.bayraktar.healthybackandneck.ui.HomePage

import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.WorkoutSession

data class RegionWorkout(val title: Int, val category: String, val level: Int, val image: Int)
data class TrainingRegion(val key: String, val title: Int, val icon: Int, val silhouette: Int, val workouts: List<RegionWorkout>)

object TrainingCatalog {
    val regions = listOf(
        TrainingRegion("arms", R.string.region_arms, R.drawable.focus_arms_photo, R.drawable.body_map_arms, listOf(
            RegionWorkout(R.string.arm_desc1, "arm", 1, R.drawable.kol11),
            RegionWorkout(R.string.arm_desc2, "arm", 2, R.drawable.kol2),
            RegionWorkout(R.string.arm_desc3, "arm", 3, R.drawable.kol4))),
        TrainingRegion("back", R.string.region_back_neck, R.drawable.focus_back_photo, R.drawable.body_map_back, listOf(
            RegionWorkout(R.string.back_desc1, "bellyneck", 1, R.drawable.back2),
            RegionWorkout(R.string.back_desc2, "bellyneck", 2, R.drawable.back1),
            RegionWorkout(R.string.neck_desc1, "bellyneck", 3, R.drawable.neck1),
            RegionWorkout(R.string.neck_desc2, "bellyneck", 4, R.drawable.neck2))),
        TrainingRegion("abs", R.string.region_abs, R.drawable.focus_abs_photo, R.drawable.body_map_abs, listOf(
            RegionWorkout(R.string.abs_desc1, "abs", 1, R.drawable.abs1),
            RegionWorkout(R.string.abs_desc2, "abs", 2, R.drawable.abs2),
            RegionWorkout(R.string.abs_desc3, "abs", 3, R.drawable.abs3))),
        TrainingRegion("legs", R.string.region_legs_glutes, R.drawable.focus_legs_photo, R.drawable.body_map_legs, listOf(
            RegionWorkout(R.string.leg_desc1, "legbutt", 1, R.drawable.bacak2),
            RegionWorkout(R.string.leg_desc2, "legbutt", 2, R.drawable.popo2),
            RegionWorkout(R.string.leg_desc3, "legbutt", 3, R.drawable.popo1),
            RegionWorkout(R.string.leg_desc4, "legbutt", 4, R.drawable.pop2))),
        TrainingRegion("warmup", R.string.region_warmup, R.drawable.region_full, R.drawable.body_map_full, listOf(
            RegionWorkout(R.string.warmup_desc1, "stretch", 1, R.drawable.warmup1),
            RegionWorkout(R.string.warmup_desc2, "stretch", 2, R.drawable.isinma2),
            RegionWorkout(R.string.warmup_desc3, "stretch", 3, R.drawable.isinma1))),
    )
}

data class ProgramResumeTarget(val level: Int, val day: Int)

/** Only recorded main-program sessions can populate the continue card. */
fun programResumeTarget(sessions: List<WorkoutSession>): ProgramResumeTarget? {
    val last = sessions.filter { it.level in 1..3 && it.day in 1..21 }
        .maxByOrNull(WorkoutSession::endedAt) ?: return null
    return ProgramResumeTarget(last.level, if (last.isComplete) (last.day + 1).coerceAtMost(21) else last.day)
}
