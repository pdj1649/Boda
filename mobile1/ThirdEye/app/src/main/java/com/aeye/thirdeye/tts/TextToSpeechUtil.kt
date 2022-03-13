package com.aeye.thirdeye.vibrator

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeechUtil(val activity: Activity,
                       val txt: String,
                       val pit: Double = 1.0,
                       val spd: Double = 1.0): TextToSpeech.OnInitListener {

    val ttsCustom = TextToSpeech(activity, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = ttsCustom.setLanguage(Locale.KOREA)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("TTS", "unsupported language")
            } else {
                ttsCustom.setPitch(pit.toFloat())
                ttsCustom.setSpeechRate(spd.toFloat())
                speakTxt(txt)
            }
        } else {
            Log.d("TTS", "failed initialization")
        }
    }

    private fun speakTxt(txt: String) {
        ttsCustom.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}