package com.emprendecoders.voicenotifier

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.emprendecoders.voicenotifier.database.AppDatabase
import com.emprendecoders.voicenotifier.dto.AppPermissionDto
import com.emprendecoders.voicenotifier.util.AppsPermissionLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var database: AppDatabase
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("NotificationListener", "Notificación eliminada de: ${sbn?.packageName}")
        super.onNotificationRemoved(sbn)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName
        val extras = sbn.notification?.extras ?: return
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT)
            ?: extras.getString(Notification.EXTRA_TEXT_LINES)
            ?: ""

        val appName = AppsPermissionLiveData.getNameByPackage(packageName)
        if (appName.isBlank() || shouldIgnoreNotification(appName, title, text)) return

        processNotification(appName, title, text)
    }

    private fun shouldIgnoreNotification(appName: String, title: String?, text: String?): Boolean {
        return appName.lowercase().contains("whatsapp") && (
                title?.lowercase() == "whatsapp" ||
                        text?.lowercase()?.contains("mensajes nuevos") == true ||
                        text?.lowercase()?.contains("new messages") == true
                )
    }

    private fun processNotification(appName: String, title: String, text: String) {
        serviceScope.launch {
            val appPermission = database.appPermissionDao().getAppByPackageName(packageName)
            if (appPermission?.enabled == false) return@launch

            sendNotificationBroadcast(appName, title, text)
        }
    }

    private fun sendNotificationBroadcast(appName: String, title: String, text: String) {
        val intent = Intent("com.emprendecoders.voicenotifier.NOTIFICATION_LISTENER")
            .setPackage("com.emprendecoders.voicenotifier")
            .apply {
                putExtra("app", appName)
                putExtra("title", title)
                putExtra("text", text)
            }
        sendBroadcast(intent)
    }

    private fun initializeService() {
        database = AppDatabase.getDatabase(applicationContext)
        setupRemoteConfig()
        fetchAppPermissions()
    }

    private fun setupRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    private fun fetchAppPermissions() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val remoteJson = remoteConfig.getString("apps_permission")
                    val permissions = Gson().fromJson(remoteJson, Array<AppPermissionDto>::class.java).toList()
                    AppsPermissionLiveData.updateList(permissions)
                }
            }
    }

}