package com.emprendecoders.voicenotifier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPermissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(permission: AppPermissionEntity);

    @Query("SELECT * FROM app_permission")
    fun getAll(): Flow<List<AppPermissionEntity>>

    @Query("SELECT * FROM app_permission WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): AppPermissionEntity?
}