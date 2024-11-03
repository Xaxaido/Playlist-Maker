package com.practicum.playlistmaker.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce(
    private val delay: Long = 500L,
    private val scope: CoroutineScope,
    private var action: () -> Unit = {},
) {

    private var job: Job? = null
    val isRunning get() = job?.isActive ?: false

    fun start(isLoop: Boolean = false) {
        stop()
        job = scope.launch {
            do {
                delay(delay)
                if (isRunning) {
                    action()
                }
            } while (isLoop)
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}