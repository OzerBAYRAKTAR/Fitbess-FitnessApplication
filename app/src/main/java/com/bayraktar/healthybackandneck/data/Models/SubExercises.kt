package com.bayraktar.healthybackandneck.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class SubExercises(
    @PrimaryKey(autoGenerate = false) val exerciseId: Int,
    val titleName : String,
    val level : Int,
    val exerciseName: String,
    val exerciseDescription: String,
    val image: Int,
    var isFavourite: Boolean
)
