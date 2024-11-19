package com.emprendecoders.voicenotifier.database.repository

import com.emprendecoders.voicenotifier.database.dao.NotificationConfigDao
import com.emprendecoders.voicenotifier.database.model.NotificationConfigEntity
import com.emprendecoders.voicenotifier.util.DBContants.TABLE_CONFIG_SWITCH_READ_NOTIFY

class NotificationConfigRepository(private val dao: NotificationConfigDao) {

    suspend fun getConfigById(id: Int): NotificationConfigEntity? = dao.getNotificationConfigById(id)

    suspend fun updateConfig(config: NotificationConfigEntity) {
        dao.insert(config)
    }

    suspend fun initializeDefaultConfig() {
        if (dao.countConfigWithId(TABLE_CONFIG_SWITCH_READ_NOTIFY) == 0) {
            dao.insert(NotificationConfigEntity(id = TABLE_CONFIG_SWITCH_READ_NOTIFY, enabled = false))
        }
    }
}