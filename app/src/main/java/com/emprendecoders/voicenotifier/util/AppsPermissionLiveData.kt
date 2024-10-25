package com.emprendecoders.voicenotifier.util

import androidx.lifecycle.MutableLiveData
import com.emprendecoders.voicenotifier.dto.AppPermission

object AppsPermissionLiveData {

    private val _listado = MutableLiveData<List<AppPermission>>()

    fun updateList(list: List<AppPermission>) {
        _listado.postValue(list)
    }

    fun getNameByPackage(packageName: String): String {
        return _listado.value?.find { it.packageName == packageName }?.name ?: ""
    }

}