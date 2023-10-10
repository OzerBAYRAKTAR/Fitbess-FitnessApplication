package com.bayraktar.healthybackandneck.Models.FoodModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodItems (
    val id: Int,
    val title: String,
    val calori: Int,
    val protein: String,
    val carb: String,
    val yag: String,
    val vitaminC: String,
    val sodium: String,
    val calsium: String,
    val potasium: String,
    val magnesium: String,
    val iron: String,
    val imageFood: Int
): Parcelable