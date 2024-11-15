package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.domain.model.Track

interface PlayerState {

    object Ready : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Stop : PlayerState
    class TrackData(
        val track: Track
    ) : PlayerState
    class CurrentTime(
        val time: String
    ) : PlayerState
    class BufferedProgress(
        val progress: Int
    ) : PlayerState
    class Description(
        val result: TrackDescription
    ) : PlayerState
    class IsFavorite(
        val isFavorite: Boolean,
        val shouldPlayAnimation: Boolean = true
    ) : PlayerState
}