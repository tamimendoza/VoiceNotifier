package com.emprendecoders.voicenotifier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emprendecoders.voicenotifier.database.model.NotificationConfig

@Dao
interface NotificationConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationConfig: NotificationConfig)

    @Query("SELECT * FROM notification_config WHERE id = :id")
    suspend fun getNotificationConfigById(id: Int): NotificationConfig?

    @Query("SELECT COUNT(*) FROM notification_config WHERE id = :id")
    suspend fun countConfigWithId(id: Int): Int

}