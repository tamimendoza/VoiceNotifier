package com.emprendecoders.voicenotifier.util

import android.content.Context
import android.provider.Settings
import com.emprendecoders.voicenotifier.constant.AppConstant.ENABLED_NOTIFICATION_LISTENERS

fun isNotificationServiceEnabled(context: Context): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        context.contentResolver, ENABLED_NOTIFICATION_LISTENERS
    )
    val packageName = context.packageName
    return enabledNotificationListeners?.contains(packageName) == true
}