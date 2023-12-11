package com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "sub_exercise_day_exercise",primaryKeys = ["exerciseId"])
data class SubExerciseDayExercise(
    @SerializedName("ExerciseId")
    val exerciseId: Int,
    @SerializedName("titleName")
    val titleName: String,
    @SerializedName("ExerciseName")
    val exerciseName: String,
    @SerializedName("ExerciseDescription")
    val exerciseDescription: String,
    @SerializedName("Image")
    val image: Int,
    @SerializedName("level")
    var level: Int,

    @SerializedName("isChecked")
    var isChecked: Boolean = false,

    @SerializedName("isFavourite")
    var isFavourite: Boolean = false
): Parcelable