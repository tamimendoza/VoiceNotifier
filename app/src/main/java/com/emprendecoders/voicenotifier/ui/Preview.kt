package com.emprendecoders.voicenotifier.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.emprendecoders.voicenotifier.ui.theme.VoiceNotifierTheme

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoiceNotifierTheme {
        NotificationReaderScreen(
            name = "Notification Reader",
            btnTextPlay = "Play",
            semanticsButtonPlay = "Play",
            btnTextStop = "Stop",
            semanticsButtonStop = "Stop",
            btnPermissionReadText = "Read Text",
            isReading = false,
            clickPlay = {},
            clickStop = {},
            notficationText = "...",
            isReadTextNotification = true,
            semanticsTextSwitchReadEnable = "",
            clickSwitchReadTextNotification = {},
            semanticsSwitchs = "",
            viewModelApp = null,
        )
    }
}