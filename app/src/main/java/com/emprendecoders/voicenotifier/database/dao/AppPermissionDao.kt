package com.emprendecoders.voicenotifier.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.emprendecoders.voicenotifier.database.AppPermissionEntity

@Dao
interface AppPermissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(permission: AppPermissionEntity);

}