package com.emprendecoders.voicenotifier.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context, onInit: (Boolean) -> Unit) {

    private var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                var languageDevice = tts.setLanguage(Locale.getDefault())
                if (languageDevice == TextToSpeech.LANG_MISSING_DATA || languageDevice == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                    onInit(false)
                } else {
                    onInit(true)
                }
            } else {
                Log.e("TTS", "Initialization failed")
                onInit(false)
            }
        }
    }

    fun speak(text: String, context: Context) {
        if (!tts.isSpeaking) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts.shutdown()
    }

}