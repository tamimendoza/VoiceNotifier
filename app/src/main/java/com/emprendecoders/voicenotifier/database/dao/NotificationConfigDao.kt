package com.emprendecoders.voicenotifier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emprendecoders.voicenotifier.database.model.NotificationConfigEntity

@Dao
interface NotificationConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationConfig: NotificationConfigEntity)

    @Query("SELECT * FROM notification_config WHERE id = :id")
    suspend fun getNotificationConfigById(id: Int): NotificationConfigEntity?

    @Query("SELECT COUNT(*) FROM notification_config WHERE id = :id")
    suspend fun countConfigWithId(id: Int): Int

}