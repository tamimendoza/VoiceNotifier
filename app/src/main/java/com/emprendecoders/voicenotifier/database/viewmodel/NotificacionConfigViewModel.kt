package com.emprendecoders.voicenotifier.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emprendecoders.voicenotifier.database.AppDatabase
import com.emprendecoders.voicenotifier.database.model.NotificationConfigEntity
import com.emprendecoders.voicenotifier.database.repository.NotificationConfigRepository
import kotlinx.coroutines.launch

class NotificacionConfigViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: NotificationConfigRepository

    init {
        val dao = AppDatabase.getDatabase(app).notificationConfigDao()
        repository = NotificationConfigRepository(dao)
    }

    fun initConfigIfNeeded() = viewModelScope.launch {
        repository.initializeDefaultConfig()
    }

    suspend fun getConfigById(id: Int): NotificationConfigEntity? = repository.getConfigById(id)

    fun updateConfig(config: NotificationConfigEntity) = viewModelScope.launch {
        repository.updateConfig(config)
    }

}