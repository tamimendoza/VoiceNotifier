package com.emprendecoders.voicenotifier.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationReaderScreen(
    name: String,
    modifier: Modifier = Modifier,
    btnTextPlay: String,
    btnTextStop: String,
    isReading: Boolean,
    clickPlay: () -> Unit,
    clickStop: () -> Unit,
    notficationText: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) }
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            Button(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    if (isReading) {
                        clickStop()
                    } else {
                        clickPlay()
                    }
                }
            ) {
                Text(if (isReading) btnTextStop else btnTextPlay)
            }
            Text(text = notficationText, modifier = modifier.padding(16.dp))
        }
    }
}