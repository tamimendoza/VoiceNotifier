package com.emprendecoders.voicenotifier.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver(private val onNotificationReceved: (String, String, String) -> Unit): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val app = intent?.getStringExtra("app") ?: ""
        val title = intent?.getStringExtra("title") ?: ""
        val text = intent?.getStringExtra("text") ?: ""

        onNotificationReceved(app, title, text)
    }
}