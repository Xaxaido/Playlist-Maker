package com.practicum.playlistmaker.common

class Event<out T>(
    private val state: T,
) {
    private var isHandled = false

    fun getState(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            state
        }
    }
}