package com.practicum.playlistmaker.extension.util

import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce(
    private val delay: Long = 500L,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val onStart: () -> Unit
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    private var job: Job? = null

    @UiThread
    fun start(isLoop: Boolean = false) {
        stop()
        job = scope.launch {
            do {
                delay(delay)
                onStart()
            } while (isLoop)
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}