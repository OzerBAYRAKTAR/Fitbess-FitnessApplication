package com.bayraktar.healthybackandneck

import com.bayraktar.healthybackandneck.ui.Statistics.*
import org.junit.Assert.*
import org.junit.Test

class BodyCalculationsTest {
    @Test fun adultBmiClassificationUsesRawNonOverlappingCdcBoundaries() {
        listOf(18.499 to BmiCategory.UNDERWEIGHT, 18.5 to BmiCategory.HEALTHY,
            24.999 to BmiCategory.HEALTHY, 25.0 to BmiCategory.OVERWEIGHT,
            29.999 to BmiCategory.OVERWEIGHT, 30.0 to BmiCategory.OBESITY).forEach { (value, expected) ->
            assertEquals(expected, BodyCalculations.bmiCategory(value))
        }
        assertNull(BodyCalculations.bmiCategory(Double.NaN))
        assertNull(BodyCalculations.bmiCategory(0.0))
        assertEquals(24.691358, BodyCalculations.bmi(180, 80)!!, .000001)
    }

    @Test fun invalidInputsDoNotProduceSaveableResults() {
        assertNull(BodyCalculations.bmi(0, 80))
        assertNull(BodyCalculations.bmi(180, -1))
        assertNull(BodyCalculations.bodyFat(BodySex.MALE, 180, 90, 40, 0))
        assertNull(BodyCalculations.bodyFat(BodySex.FEMALE, 170, 35, 90, 0))
        assertNull(BodyCalculations.dailyCalories(null, 180, 80, 30, 1))
        assertNull(BodyCalculations.dailyCalories(BodySex.MALE, 180, 80, 0, 1))
        assertNull(BodyCalculations.dailyCalories(BodySex.MALE, 180, 80, 30, 5))
    }

    @Test fun bodyFatCoefficientsAndCentimeterInputsArePreservedAndMaleHipIsUnused() {
        assertEquals(18.36749515, BodyCalculations.bodyFat(BodySex.MALE, 180, 40, 90, 0)!!, .000001)
        assertEquals(BodyCalculations.bodyFat(BodySex.MALE, 180, 40, 90, 0), BodyCalculations.bodyFat(BodySex.MALE, 180, 40, 90, 100))
        assertEquals(34.33707518, BodyCalculations.bodyFat(BodySex.FEMALE, 170, 35, 90, 100)!!, .000001)
    }

    @Test fun dailyCalorieFormulaAndAllExistingActivityMultipliersArePreserved() {
        listOf(1.2, 1.3, 1.4, 1.5).forEachIndexed { index, multiplier ->
            assertEquals(1863.4 * multiplier, BodyCalculations.dailyCalories(BodySex.MALE, 180, 80, 30, index + 1)!!, .000001)
            assertEquals(1450.9 * multiplier, BodyCalculations.dailyCalories(BodySex.FEMALE, 170, 65, 30, index + 1)!!, .000001)
        }
    }

    @Test fun legacySexValuesRestoreAcrossEveryShippedLanguage() {
        listOf("Erkek", "Male", "Männlich", "Mann", "Мужчина", "male").forEach { assertEquals(BodySex.MALE, BodyCalculations.storedSex(it)) }
        listOf("Kadın", "Female", "Frau", "Женщина", "female").forEach { assertEquals(BodySex.FEMALE, BodyCalculations.storedSex(it)) }
        assertNull(BodyCalculations.storedSex(""))
    }

    @Test fun savedEstimatesMustMatchCurrentMeasurementsAndLegacyDecimalFormat() {
        val snapshot = BodyCalculatorSnapshot(height = 180, weight = 80, age = 30, activity = 1, gender = "Erkek",
            neck = 40, waist = 90, savedBmi = "24,69", savedCalories = "2236,08", savedFat = "18,367")
        assertNotNull(snapshot.validSavedBmi)
        assertNotNull(snapshot.validSavedCalories)
        assertNotNull(snapshot.validSavedFat)
        assertNull(snapshot.copy(weight = 81).validSavedBmi)
        assertNull(snapshot.copy(activity = 2).validSavedCalories)
        assertNull(snapshot.copy(neck = 41).validSavedFat)
        assertNull(snapshot.copy(savedCalories = "2,236.08").validSavedCalories)
        assertNull(snapshot.copy(savedCalories = "2,236").validSavedCalories)
        assertNull(snapshot.copy(savedBmi = "NaN").validSavedBmi)
    }
}
