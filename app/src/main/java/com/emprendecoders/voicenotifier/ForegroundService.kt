package com.emprendecoders.voicenotifier

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.emprendecoders.voicenotifier.constant.AppConstant

class ForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.CHANNEL_ID,
                AppConstant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val appTitle = getString(R.string.app_title)
        val appNotificacionBackground = getString(R.string.read_notificacion_background)
        val notification = NotificationCompat.Builder(this, AppConstant.CHANNEL_ID)
            .setContentTitle(appTitle)
            .setContentText(appNotificacionBackground)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(AppConstant.NOTIFICACION_ID, notification)
    }

}