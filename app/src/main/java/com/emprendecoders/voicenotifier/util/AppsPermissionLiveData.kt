package com.emprendecoders.voicenotifier.util

import androidx.lifecycle.MutableLiveData
import com.emprendecoders.voicenotifier.dto.AppPermissionDto

object AppsPermissionLiveData {

    private val _listado = MutableLiveData<List<AppPermissionDto>>()

    fun updateList(list: List<AppPermissionDto>) {
        _listado.postValue(list)
    }

    fun getNameByPackage(packageName: String): String {
        return _listado.value?.find { it.packageName == packageName }?.name ?: ""
    }

}