package com.practicum.playlistmaker.common.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce(
    private val delay: Long = 500L,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private var action: () -> Unit = {},
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private var job: Job? = null
    val isRunning get() = job?.isActive ?: false

    fun addAction(action: () -> Unit) {
        this.action = action
    }

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