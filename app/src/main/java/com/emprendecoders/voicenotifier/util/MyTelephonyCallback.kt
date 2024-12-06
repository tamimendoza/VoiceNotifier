package com.emprendecoders.voicenotifier.util

import android.os.Build
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
class MyTelephonyCallback(private val onCallStateChanged: (Boolean) -> Unit) : TelephonyCallback(),
    TelephonyCallback.CallStateListener {
    override fun onCallStateChanged(state: Int) {
        val isInCall = state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING
        onCallStateChanged(isInCall)
    }
}