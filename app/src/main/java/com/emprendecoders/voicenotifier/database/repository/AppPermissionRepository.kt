package com.emprendecoders.voicenotifier.database.repository

import com.emprendecoders.voicenotifier.database.dao.AppPermissionDao
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity
import kotlinx.coroutines.flow.Flow

class AppPermissionRepository(private val appPermissionDao: AppPermissionDao) {

    suspend fun insert(entity: AppPermissionEntity) {
        appPermissionDao.insert(entity)
    }

    fun getAll(): Flow<List<AppPermissionEntity>> {
        return appPermissionDao.getAll()
    }

    suspend fun getAppByPackageName(packageName: String): AppPermissionEntity? {
        return appPermissionDao.getAppByPackageName(packageName)
    }

}