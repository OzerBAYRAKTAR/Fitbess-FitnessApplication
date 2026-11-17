package com.bayraktar.healthybackandneck.ui.Statistics

import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun DataStoreManage.calculatorSnapshots(): Flow<BodyCalculatorSnapshot> {
    val basics = combine(getHeight(), getWeight(), getAge(), getActivityLevel(), getGender()) { h, w, a, activity, gender ->
        BodyCalculatorSnapshot(h, w, a, activity, gender)
    }
    val circumferences = combine(getNeck(), getBelly(), getBooty()) { neck, waist, hip -> Triple(neck, waist, hip) }
    val results = combine(getIndeks(), getCalorie(), getFatRate()) { bmi, calories, fat -> Triple(bmi, calories, fat) }
    return combine(basics, circumferences, results) { basic, measures, saved ->
        basic.copy(neck = measures.first, waist = measures.second, hip = measures.third,
            savedBmi = saved.first, savedCalories = saved.second, savedFat = saved.third)
    }
}
