package com.emprendecoders.voicenotifier.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity

@Composable
fun RowItem(
    item: AppPermissionEntity,
    semanticsSwitchs: String,
    onPermissionChanged: (AppPermissionEntity) -> Unit
) {
    var isActive by remember { mutableStateOf(item.enabled) }

    Row {
        Switch(
            checked = isActive,
            onCheckedChange = { isChecked ->
                var app = AppPermissionEntity(
                    packageName = item.packageName,
                    name = item.name,
                    enabled = isChecked
                )
                isActive = isChecked
                onPermissionChanged(app)
            },
            modifier = Modifier
                .padding(start = 16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = semanticsSwitchs.format(item.name)
                }
        )
        Text(text = item.name, modifier = Modifier.padding(start = 16.dp, top = 12.dp))
    }
}