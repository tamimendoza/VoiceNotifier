package com.emprendecoders.voicenotifier

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.emprendecoders.voicenotifier.constant.AppConstant.APPS_PERMISSION
import com.emprendecoders.voicenotifier.constant.AppConstant.INTENT_ACTION_FILTER
import com.emprendecoders.voicenotifier.constant.AppConstant.NOTIFY_APP
import com.emprendecoders.voicenotifier.constant.AppConstant.NOTIFY_TEXT
import com.emprendecoders.voicenotifier.constant.AppConstant.NOTIFY_TITLE
import com.emprendecoders.voicenotifier.constant.AppConstant.PACKAGE_NAME
import com.emprendecoders.voicenotifier.constant.AppConstant.WHATSAPP
import com.emprendecoders.voicenotifier.constant.AppConstant.WHATSAPP_NEW_MESSAGES_EN
import com.emprendecoders.voicenotifier.constant.AppConstant.WHATSAPP_NEW_MESSAGES_ES
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

        processNotification(packageName, appName, title, text)
    }

    private fun shouldIgnoreNotification(appName: String, title: String?, text: String?): Boolean {
        return appName.lowercase().contains(WHATSAPP) && (
                title?.isBlank() == true ||
                        title?.lowercase() == WHATSAPP ||
                        text?.lowercase()?.contains(WHATSAPP_NEW_MESSAGES_ES) == true ||
                        text?.lowercase()?.contains(WHATSAPP_NEW_MESSAGES_EN) == true
                )
    }

    private fun processNotification(
        packageName: String,
        appName: String,
        title: String,
        text: String
    ) {
        serviceScope.launch {
            val appPermission = database.appPermissionDao().getAppByPackageName(packageName)
            if (appPermission?.enabled == true) {
                sendNotificationBroadcast(appName, title, text)
            }
        }
    }

    private fun sendNotificationBroadcast(appName: String, title: String, text: String) {
        val intent = Intent(INTENT_ACTION_FILTER)
            .setPackage(PACKAGE_NAME)
            .apply {
                putExtra(NOTIFY_APP, appName)
                putExtra(NOTIFY_TITLE, title)
                putExtra(NOTIFY_TEXT, text)
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
                val remoteJson = remoteConfig.getString(APPS_PERMISSION)
                val permissions =
                    Gson().fromJson(remoteJson, Array<AppPermissionDto>::class.java).toList()
                AppsPermissionLiveData.updateList(permissions)
            }
        }
    }

}