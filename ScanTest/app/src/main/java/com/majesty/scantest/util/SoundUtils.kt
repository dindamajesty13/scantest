package com.majesty.scantest.util

import android.media.AudioManager
import android.media.ToneGenerator

object SoundUtils {
    private var toneGenerator: ToneGenerator? = null

    private val lock = Any()

    private fun getOrCreateToneGenerator(): ToneGenerator {
        synchronized(lock) {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME)
            }
            return toneGenerator!!
        }
    }

    fun startSound() {
        try {
            getOrCreateToneGenerator().startTone(ToneGenerator.TONE_PROP_BEEP)
        }catch (e: Exception) {
            println("Exception when playing sound $e")
        }
    }

    fun releaseSound() {
        synchronized(lock) {
            toneGenerator?.release()
            toneGenerator = null
        }
    }
}