package com.emprendecoders.voicenotifier.util

import android.content.Context
import android.provider.Settings

fun isNotificationServiceEnabled(context: Context): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        context.contentResolver, "enabled_notification_listeners"
    )
    val packageName = context.packageName
    return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName)
}