package com.emprendecoders.voicenotifier.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.emprendecoders.voicenotifier.database.model.AppPermissionEntity
import com.emprendecoders.voicenotifier.database.viewmodel.AppPermissionViewModel
import com.emprendecoders.voicenotifier.util.remoteConfig
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationReaderScreen(
    name: String,
    modifier: Modifier = Modifier,
    btnTextPlay: String,
    semanticsButtonPlay: String,
    btnTextStop: String,
    semanticsButtonStop: String,
    btnPermissionReadText: String,
    isReading: Boolean,
    clickPlay: () -> Unit,
    clickStop: () -> Unit,
    notficationText: String,
    semanticsTextSwitchReadEnable: String,
    isReadTextNotification: Boolean,
    clickSwitchReadTextNotification: (Boolean) -> Unit,
    semanticsSwitchs: String,
    viewModelApp: AppPermissionViewModel?
) {
    val listado = remember { mutableStateListOf<AppPermissionEntity>() }

    LaunchedEffect(Unit) {
        coroutineScope {
            val remoteList = remoteConfig(viewModelApp)
            listado.clear()
            listado.addAll(remoteList)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(name) })
    }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription =
                            if (isReading) semanticsButtonStop else semanticsButtonPlay
                    },
                onClick = {
                    if (isReading) {
                        clickStop()
                    } else {
                        clickPlay()
                    }
                },
            ) {
                Text(if (isReading) btnTextStop else btnTextPlay)
            }
            Text(text = notficationText, modifier = modifier.padding(16.dp))

            Row {
                Switch(checked = isReadTextNotification,
                    onCheckedChange = {
                        clickSwitchReadTextNotification(it)
                    },
                    modifier = modifier
                        .padding(start = 16.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = semanticsTextSwitchReadEnable
                        })
                Text(
                    text = btnPermissionReadText,
                    modifier = modifier.padding(start = 16.dp, top = 12.dp)
                )
            }

            DynamicList(listado = listado, semanticsSwitchs = semanticsSwitchs, onPermissionChanged = { item ->
                viewModelApp?.insert(item)
            })
        }
    }
}