package com.bayraktar.healthybackandneck.data.Models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "count_table")
data class CountModel(
   @PrimaryKey(autoGenerate = false)
   var id: Int = 1,
   var count : Int = 0
)
