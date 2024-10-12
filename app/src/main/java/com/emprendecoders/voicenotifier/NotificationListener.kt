package com.emprendecoders.voicenotifier

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName
        val title = sbn?.notification?.extras?.getString("android.title")
        val text = sbn?.notification?.extras?.getString("android.text")

        if (packageName == "com.whatsapp.w4b") {
            if (title?.lowercase() == "whatsapp") return
            if (text?.lowercase()?.contains("mensajes nuevos") == true) return
        }

        val intent = Intent("com.emprendecoders.voicenotifier.NOTIFICATION_LISTENER")
        intent.setPackage("com.emprendecoders.voicenotifier")

        intent.putExtra("app", packageName)
        intent.putExtra("title", title)
        intent.putExtra("text", text)

        sendBroadcast(intent)
    }

}