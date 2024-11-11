package com.emprendecoders.voicenotifier.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emprendecoders.voicenotifier.database.dao.NotificationConfigDao
import com.emprendecoders.voicenotifier.database.model.NotificationConfig

@Database(
    entities = [NotificationConfig::class, AppPermissionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationConfigDao(): NotificationConfigDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "voicenotifier.db"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }

}