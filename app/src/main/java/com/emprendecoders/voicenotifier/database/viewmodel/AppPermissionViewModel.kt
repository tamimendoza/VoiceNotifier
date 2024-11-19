package com.emprendecoders.voicenotifier.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emprendecoders.voicenotifier.database.AppDatabase
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity
import com.emprendecoders.voicenotifier.database.repository.AppPermissionRepository
import kotlinx.coroutines.launch

class AppPermissionViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: AppPermissionRepository

    init {
        val dao = AppDatabase.getDatabase(app).appPermissionDao()
        repository = AppPermissionRepository(dao)
    }

    fun insert(entity: AppPermissionEntity) = viewModelScope.launch {
        repository.insert(entity)
    }

    fun getAllPermissions() = repository.getAll()

    suspend fun getAppByPackageName(packageName: String): AppPermissionEntity? {
        return repository.getAppByPackageName(packageName)
    }
}