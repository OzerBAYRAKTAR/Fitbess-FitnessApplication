package com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity
data class ExerciseDay(
    @PrimaryKey(autoGenerate = false) val dayId: Int = 0,
    val day: Int,
    val exerciseCount: Int,
    val exerciseTime: Int,
    val exerciseKcal: Int,
    val homeId: Int,
): Parcelable  {
    // Convert ExerciseDay object to JSON string
    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    // Convert JSON string to ExerciseDay object
    companion object {
        fun fromJson(json: String): ExerciseDay {
            val gson = Gson()
            return gson.fromJson(json, ExerciseDay::class.java)
        }
    }
}
