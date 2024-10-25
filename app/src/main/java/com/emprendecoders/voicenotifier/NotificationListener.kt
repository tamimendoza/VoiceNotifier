package com.emprendecoders.voicenotifier

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.emprendecoders.voicenotifier.dto.AppPermission
import com.emprendecoders.voicenotifier.util.AppsPermissionLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson

class NotificationListener : NotificationListenerService() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()

        remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        fetchAppPermissions()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName
        val title = sbn?.notification?.extras?.getString("android.title")
        val text = sbn?.notification?.extras?.getString("android.text")

        val nombreApp = AppsPermissionLiveData.getNameByPackage(packageName.toString())

        if (nombreApp == "Whatsapp") {
            if (title?.lowercase() == "whatsapp") return
            if (text?.lowercase()?.contains("mensajes nuevos") == true) return
        }

        if (nombreApp.isNotBlank()) {
            val intent = Intent("com.emprendecoders.voicenotifier.NOTIFICATION_LISTENER")
            intent.setPackage("com.emprendecoders.voicenotifier")

            intent.putExtra("app", nombreApp)
            intent.putExtra("title", title)
            intent.putExtra("text", text)

            sendBroadcast(intent)
        }
    }

    private fun fetchAppPermissions() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val remoteJson = remoteConfig.getString("apps_permission")
                    val permissions =
                        Gson().fromJson(remoteJson, Array<AppPermission>::class.java).toList()
                    AppsPermissionLiveData.updateList(permissions)
                }
            }
    }

}