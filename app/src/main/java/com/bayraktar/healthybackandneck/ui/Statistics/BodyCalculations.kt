package com.bayraktar.healthybackandneck.ui.Statistics

import kotlin.math.abs
import kotlin.math.log10

enum class BodySex { MALE, FEMALE }
enum class BmiCategory { UNDERWEIGHT, HEALTHY, OVERWEIGHT, OBESITY }

/** Existing centimeter/kilogram formulae; presentation and validation do not alter coefficients. */
object BodyCalculations {
    fun bmi(height: Int, weight: Int): Double? =
        if (height > 0 && weight > 0) (weight / (height / 100.0).let { it * it }).takeIf { it.isFinite() && it > 0 } else null

    fun bmiCategory(value: Double): BmiCategory? = when {
        !value.isFinite() || value <= 0 -> null
        value < 18.5 -> BmiCategory.UNDERWEIGHT
        value < 25 -> BmiCategory.HEALTHY
        value < 30 -> BmiCategory.OVERWEIGHT
        else -> BmiCategory.OBESITY
    }

    fun bodyFat(sex: BodySex?, height: Int, neck: Int, waist: Int, hip: Int): Double? {
        if (sex == null || height <= 0 || neck <= 0 || waist <= 0 || (sex == BodySex.FEMALE && hip <= 0)) return null
        val difference = if (sex == BodySex.MALE) waist.toDouble() - neck else waist.toDouble() + hip - neck
        if (difference <= 0) return null
        val denominator = if (sex == BodySex.MALE)
            1.0324 - 0.19077 * log10(difference) + 0.15456 * log10(height.toDouble())
        else 1.29579 - 0.35004 * log10(difference) + 0.22100 * log10(height.toDouble())
        return (495.0 / denominator - 450).takeIf { it.isFinite() && it in 3.0..100.0 }
    }

    fun dailyCalories(sex: BodySex?, height: Int, weight: Int, age: Int, activity: Int): Double? {
        if (sex == null || height <= 0 || weight <= 0 || age <= 0 || activity !in 1..4) return null
        val basal = if (sex == BodySex.MALE) 66.5 + 13.75 * weight + 5 * height - 6.77 * age
        else 655.1 + 9.56 * weight + 1.85 * height - 4.67 * age
        val multiplier = listOf(1.2, 1.3, 1.4, 1.5)[activity - 1]
        return (basal * multiplier).takeIf { it.isFinite() && it >= 3 }
    }

    /** Old releases stored translated labels. Accept all shipped locales without changing their key. */
    fun storedSex(value: String): BodySex? = when (value.trim().lowercase(java.util.Locale.ROOT)) {
        "male", "man", "erkek", "männlich", "mann", "мужчина" -> BodySex.MALE
        "female", "kadın", "frau", "weiblich", "женщина" -> BodySex.FEMALE
        else -> null
    }

    fun savedMatches(saved: String, calculated: Double?, tolerance: Double): Boolean {
        val number = saved.trim().replace(',', '.').toDoubleOrNull() ?: return false
        return number.isFinite() && calculated != null && abs(number - calculated) <= tolerance
    }
}

data class BodyCalculatorSnapshot(
    val height: Int = 0, val weight: Int = 0, val age: Int = 0, val activity: Int = 0,
    val gender: String = "", val neck: Int = 0, val waist: Int = 0, val hip: Int = 0,
    val savedBmi: String = "", val savedCalories: String = "", val savedFat: String = "",
) {
    val sex get() = BodyCalculations.storedSex(gender)
    val validSavedBmi get() = BodyCalculations.bmi(height, weight).takeIf { BodyCalculations.savedMatches(savedBmi, it, .0051) }
    val validSavedCalories get() = BodyCalculations.dailyCalories(sex, height, weight, age, activity).takeIf { BodyCalculations.savedMatches(savedCalories, it, .00051) }
    val validSavedFat get() = BodyCalculations.bodyFat(sex, height, neck, waist, hip).takeIf { BodyCalculations.savedMatches(savedFat, it, .00051) }
}
