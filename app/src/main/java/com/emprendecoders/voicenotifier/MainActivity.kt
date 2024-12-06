package com.emprendecoders.voicenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.emprendecoders.voicenotifier.database.model.NotificationConfigEntity
import com.emprendecoders.voicenotifier.database.viewmodel.AppPermissionViewModel
import com.emprendecoders.voicenotifier.database.viewmodel.NotificacionConfigViewModel
import com.emprendecoders.voicenotifier.notification.NotificationReceiver
import com.emprendecoders.voicenotifier.tts.TextToSpeechManager
import com.emprendecoders.voicenotifier.ui.NotificationReaderScreen
import com.emprendecoders.voicenotifier.ui.theme.VoiceNotifierTheme
import com.emprendecoders.voicenotifier.util.DBContants.TABLE_CONFIG_SWITCH_READ_NOTIFY
import com.emprendecoders.voicenotifier.util.MyTelephonyCallback
import com.emprendecoders.voicenotifier.util.isNotificationServiceEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var notificationReceiver: NotificationReceiver
    private lateinit var ttsManager: TextToSpeechManager
    private var telephonyCallback: MyTelephonyCallback? = null

    private val viewModelConfig: NotificacionConfigViewModel by viewModels()
    private val viewModelApp: AppPermissionViewModel by viewModels()

    private val isReading = mutableStateOf(false)
    private val isInCallStatus = mutableStateOf(false)
    private val isReadTextNotification = mutableStateOf(false)
    private val notificationText = mutableStateOf("...")
    private val permissionRequestCode = 100
    private val textPermissionReadPhoneState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        textPermissionReadPhoneState.value = getString(R.string.text_permission_phone)
        if (checkPermission()) {
            verificarAudio()
        } else {
            requestPermission()
        }

        setupInitialConfig()
        setupUI()
    }

    override fun onStart() {
        super.onStart()
        verificarAudio()
    }

    override fun onStop() {
        super.onStop()
        stopAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        ttsManager.shutdown()
        stopAudio()
    }

    private fun setupUI() {
        val appTitle = getString(R.string.app_title)
        val btnTextPlay = getString(R.string.button_play)
        val btnTextStop = getString(R.string.button_stop)
        val btnPermissionReadText = getString(R.string.text_switch_read_enable)

        setContent {
            VoiceNotifierTheme {
                NotificationReaderScreen(
                    name = appTitle,
                    btnTextPlay = btnTextPlay,
                    btnTextStop = btnTextStop,
                    btnPermissionReadText = btnPermissionReadText,
                    isReading = isReading.value,
                    clickPlay = {
                        isReading.value = true
                        val serviceIntent = Intent(this, ForegroundService::class.java)
                        ContextCompat.startForegroundService(this, serviceIntent)
                    },
                    clickStop = {
                        isReading.value = false
                        val serviceIntent = Intent(this, ForegroundService::class.java)
                        stopService(serviceIntent)
                    },
                    notficationText = notificationText.value,
                    isReadTextNotification = isReadTextNotification.value,
                    clickSwitchReadTextNotification = { isChecked ->
                        isReadTextNotification.value = isChecked
                        updateConfig(isChecked)
                    },
                    viewModelApp = viewModelApp
                )
            }
        }
    }

    private fun setupInitialConfig() {
        receiveNotification()
        verifyRegisterReceiver()
        verifyNotificationPermission()
        initTTS()
        loadConfig()
        getConfig()
    }

    fun receiveNotification() {
        val formatLargeNotificationText = getString(R.string.notification_large_text)
        val formatShortNotificationText = getString(R.string.notification_short_text)

        notificationReceiver = NotificationReceiver { app, title, text ->
            if (text.isBlank() || isReadTextNotification.value == false) {
                notificationText.value = formatShortNotificationText.format(app, title)
            } else {
                notificationText.value = formatLargeNotificationText.format(app, title, text)
            }
            if (isReading.value && isInCallStatus.value == false) {
                ttsManager.speak(notificationText.value, this@MainActivity)
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
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    fun initTTS() {
        ttsManager = TextToSpeechManager(this) { success ->
            if (!success) Log.e("TTS", "Initialization failed")
        }
    }

    fun loadConfig() {
        viewModelConfig.initConfigIfNeeded()
    }

    fun getConfig() {
        lifecycleScope.launch(Dispatchers.Main) {
            isReadTextNotification.value =
                viewModelConfig.getConfigById(TABLE_CONFIG_SWITCH_READ_NOTIFY)?.enabled == true
        }
    }

    fun updateConfig(enabled: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModelConfig.updateConfig(
                NotificationConfigEntity(
                    id = TABLE_CONFIG_SWITCH_READ_NOTIFY,
                    enabled
                )
            )
        }
    }

    fun verificarAudio() {
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

                telephonyCallback = MyTelephonyCallback { isInCall ->
                    isInCallStatus.value = isInCall
                }

                telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback!!)
            }
        } else {
            Toast.makeText(this, textPermissionReadPhoneState.value, Toast.LENGTH_LONG).show()
        }
    }

    fun stopAudio() {
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

                telephonyCallback?.let {
                    telephonyManager.unregisterTelephonyCallback(it)
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_PHONE_STATE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_PHONE_STATE),
            permissionRequestCode
        )
    }

}