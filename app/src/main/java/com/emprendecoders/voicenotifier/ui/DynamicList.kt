package com.emprendecoders.voicenotifier.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity

@Composable
fun DynamicList(listado: List<AppPermissionEntity>, onPermissionChanged: (AppPermissionEntity) -> Unit) {
    LazyColumn {
        items(listado) {item ->
            RowItem(item = item, onPermissionChanged = onPermissionChanged)
        }
    }
}