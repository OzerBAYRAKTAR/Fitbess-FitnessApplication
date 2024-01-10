package com.bayraktar.healthybackandneck.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motivation_notification_state")
data class MotivationNotificationState(
    @PrimaryKey val id: Int = 1,
    val isSwitchChecked: Boolean
)