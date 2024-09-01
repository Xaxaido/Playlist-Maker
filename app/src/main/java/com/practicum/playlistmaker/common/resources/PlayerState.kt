package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.TrackDescription

interface PlayerState {

    object Ready : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Stop : PlayerState
    class CurrentTime(
        val time: String
    ) : PlayerState
    class BufferedProgress(
        val progress: Int
    ) : PlayerState
    class Description(
        val result: TrackDescription
    ) : PlayerState
}