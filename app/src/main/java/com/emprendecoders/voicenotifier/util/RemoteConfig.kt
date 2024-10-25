package com.emprendecoders.voicenotifier.util

import android.util.Log
import com.emprendecoders.voicenotifier.dto.AppPermission
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.emprendecoders.voicenotifier.R
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

suspend fun RemoteConfig(): List<AppPermission> {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    try {
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val fetchResult = remoteConfig.fetchAndActivate().await()
        val remoteJson = remoteConfig.getString("apps_permission")

        return Gson().fromJson(remoteJson, Array<AppPermission>::class.java).toList()
    } catch (e: Exception) {
        Log.e("RemoteConfig", e.toString())
    }

    return emptyList()
}