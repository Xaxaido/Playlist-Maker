package com.practicum.playlistmaker.extension.util

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
    private val action: () -> Unit
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private var job: Job? = null

    fun start(isLoop: Boolean = false) {
        stop()
        job = scope.launch {
            do {
                delay(delay)
                if (isActive) {
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