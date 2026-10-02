package com.example.sound

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArcadeSoundManager {
    var isSoundEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.IO)
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 65)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    private fun playBeep(toneType: Int, durationMs: Int) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(toneType, durationMs)
            } catch (_: Exception) {
                // Ignore audio failure gracefully
            }
        }
    }

    fun playTick(stepIndex: Int) {
        val tone = if (stepIndex % 2 == 0) ToneGenerator.TONE_PROP_BEEP else ToneGenerator.TONE_PROP_BEEP2
        playBeep(tone, 25)
    }

    fun playStop() {
        playBeep(ToneGenerator.TONE_PROP_ACK, 70)
    }

    fun playWin() {
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100)
                delay(120)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 150)
            } catch (_: Exception) {
            }
        }
    }

    fun playJackpot() {
        scope.launch {
            try {
                val tones = listOf(
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_4,
                    ToneGenerator.TONE_DTMF_7,
                    ToneGenerator.TONE_DTMF_A
                )
                for (t in tones) {
                    toneGenerator?.startTone(t, 80)
                    delay(90)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun playCoin() {
        playBeep(ToneGenerator.TONE_DTMF_9, 40)
    }

    fun playClick() {
        playBeep(ToneGenerator.TONE_PROP_BEEP, 20)
    }

    fun playShakeThud() {
        playBeep(ToneGenerator.TONE_PROP_NACK, 60)
    }

    fun playDoubleUpLose() {
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 90)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 140)
            } catch (_: Exception) {
            }
        }
    }
}
