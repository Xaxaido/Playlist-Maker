package com.practicum.playlistmaker.common.utils

import android.os.Handler
import android.os.Looper

class DebounceR(
    private val delay: Long = 500L,
    action: () -> Unit = {},
) {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {

        override fun run() {
            action()
            if (isLoop) handler.postDelayed(this, delay)
        }
    }
    private var isLoop = false
    var isRunning = false

    fun start(isLoop: Boolean = false) {
        if (isRunning) stop()

        this.isLoop = isLoop
        isRunning = true
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(runnable)
    }
}