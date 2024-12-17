package com.emprendecoders.voicenotifier.util

import android.util.Log
import com.emprendecoders.voicenotifier.R
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity
import com.emprendecoders.voicenotifier.database.viewmodel.AppPermissionViewModel
import com.emprendecoders.voicenotifier.dto.AppPermissionDto
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

suspend fun remoteConfig(viewModelApp: AppPermissionViewModel?): List<AppPermissionEntity> {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    try {
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().await()
        val remoteJson = remoteConfig.getString("apps_permission")

        val remoteItems = Gson().fromJson(remoteJson, Array<AppPermissionDto>::class.java).toList()

        return remoteItems.map { item ->
            val app = viewModelApp?.getAppByPackageName(item.packageName)
            AppPermissionEntity(
                packageName = item.packageName,
                name = item.name,
                enabled = app?.enabled == true
            )
        }
    } catch (e: Exception) {
        Log.e("RemoteConfig", e.toString())
    }

    return emptyList()
}