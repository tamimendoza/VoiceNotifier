package com.emprendecoders.voicenotifier.database.repository

import com.emprendecoders.voicenotifier.database.dao.NotificationConfigDao
import com.emprendecoders.voicenotifier.database.model.NotificationConfig
import com.emprendecoders.voicenotifier.util.DBContants.TABLE_CONFIG_SWITCH_READ_NOTIFY
import kotlinx.coroutines.flow.Flow

class NotificationConfigRepository(private val dao: NotificationConfigDao) {

    suspend fun getConfigById(id: Int): NotificationConfig? = dao.getNotificationConfigById(id)

    suspend fun updateConfig(config: NotificationConfig) {
        dao.insert(config)
    }

    suspend fun initializeDefaultConfig() {
        if (dao.countConfigWithId(TABLE_CONFIG_SWITCH_READ_NOTIFY) == 0) {
            dao.insert(NotificationConfig(id = TABLE_CONFIG_SWITCH_READ_NOTIFY, enabled = false))
        }
    }
}