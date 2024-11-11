package com.emprendecoders.voicenotifier.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_config")
data class NotificationConfig(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var enabled: Boolean
)
