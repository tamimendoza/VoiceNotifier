package com.emprendecoders.voicenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.emprendecoders.voicenotifier.notification.NotificationReceiver
import com.emprendecoders.voicenotifier.tts.TextToSpeechManager
import com.emprendecoders.voicenotifier.ui.NotificationReaderScreen
import com.emprendecoders.voicenotifier.ui.theme.VoiceNotifierTheme
import com.emprendecoders.voicenotifier.util.isNotificationServiceEnabled

class MainActivity : ComponentActivity() {
    private val isReading = mutableStateOf(false)
    private val notificationText = mutableStateOf("...")

    private lateinit var notificationReceiver: NotificationReceiver
    private lateinit var ttsManager: TextToSpeechManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appTitle = getString(R.string.app_title)
        val btnTextPlay = getString(R.string.button_play)
        val btnTextStop = getString(R.string.button_stop)

        receiveNotification()
        verifyRegisterReceiver()
        verifyNotificationPermission()
        verifyTTS()

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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        ttsManager.shutdown()
    }

    fun receiveNotification() {
        val formatLargeNotificationText = getString(R.string.notification_large_text)
        val formatShortNotificationText = getString(R.string.notification_short_text)

        notificationReceiver = NotificationReceiver { app, title, text ->
            if (text.isEmpty()) {
                notificationText.value = formatShortNotificationText.format(app, title)
            } else {
                notificationText.value = formatLargeNotificationText.format(app, title, text)
            }
            if (isReading.value) {
                ttsManager.speak(text, this@MainActivity)
            }
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

    fun verifyTTS() {
        ttsManager = TextToSpeechManager(this) { success ->
            if (!success) Log.e("TTS", "Initialization failed")
        }
    }
}