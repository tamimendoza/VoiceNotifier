package com.emprendecoders.voicenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.emprendecoders.voicenotifier.notification.NotificationReceiver
import com.emprendecoders.voicenotifier.ui.NotificationReaderScreen
import com.emprendecoders.voicenotifier.ui.theme.VoiceNotifierTheme
import com.emprendecoders.voicenotifier.util.isNotificationServiceEnabled

class MainActivity : ComponentActivity() {
    private val isReading = mutableStateOf(false)
    private val notificationText = mutableStateOf("...")

    private lateinit var notificationReceiver: NotificationReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appTitle = getString(R.string.app_title)
        val btnTextPlay = getString(R.string.button_play)
        val btnTextStop = getString(R.string.button_stop)

        receiveNotification()
        verifyRegisterReceiver()
        verifyNotificationPermission()

        setContent {
            VoiceNotifierTheme {
                NotificationReaderScreen(
                    name = appTitle,
                    btnTextPlay = btnTextPlay,
                    btnTextStop = btnTextStop,
                    isReading = isReading.value,
                    clickPlay = {
                        isReading.value = true
                    },
                    clickStop = {
                        isReading.value = false
                    },
                    notficationText = notificationText.value
                )
            }
        }
    }

    fun receiveNotification() {
        notificationReceiver = NotificationReceiver { app, title, text ->
            notificationText.value = text
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun verifyRegisterReceiver() {
        val filter = IntentFilter("com.emprendecoders.voicenotifier.NOTIFICATION_LISTENER")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, filter)
        }
    }

    fun verifyNotificationPermission() {
        if (!isNotificationServiceEnabled(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
    }
}