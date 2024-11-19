package com.emprendecoders.voicenotifier.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_permission")
data class AppPermissionEntity(
    @PrimaryKey
    val packageName: String,
    val name: String,
    val enabled: Boolean
)
