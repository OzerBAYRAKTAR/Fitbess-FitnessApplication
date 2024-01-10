package com.bayraktar.healthybackandneck.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_reminder_state")
data class WaterReminderState(
    @PrimaryKey val id: Int = 1,
    val isSwitchChecked: Boolean
)