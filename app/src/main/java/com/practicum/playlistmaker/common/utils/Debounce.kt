package com.practicum.playlistmaker.common.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Debounce(
    private val delay: Long = 500L,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private var action: () -> Unit = {},
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private var job: Job? = null
    var isRunning = false

    fun addAction(action: () -> Unit) {
        this.action = action
    }

    fun start(isLoop: Boolean = false) {
        stop()
        job = scope.launch {
            do {
                delay(delay)
                if (isActive) {
                    isRunning = true
                    action()
                }
            } while (isLoop)
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
        job = null
    }
}