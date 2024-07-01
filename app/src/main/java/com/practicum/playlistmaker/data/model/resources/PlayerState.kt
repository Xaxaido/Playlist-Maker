package com.practicum.playlistmaker.data.model.resources

interface PlayerState {

    object Ready : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Stop : PlayerState
    object NoSource : PlayerState
    data class CurrentTime(
        val time: Long
    ) : PlayerState
}