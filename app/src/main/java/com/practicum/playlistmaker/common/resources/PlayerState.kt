package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.domain.model.Track

interface PlayerState {

    object Default : PlayerState
    object Prepared : PlayerState
    object Stop : PlayerState
    class Playing(
        val isPlaying: Boolean,
    ) : PlayerState
    class TrackData(
        val track: Track
    ) : PlayerState
    class CurrentTime(
        val time: String
    ) : PlayerState
    class Description(
        val result: TrackDescription
    ) : PlayerState
    class IsFavorite(
        val isFavorite: Boolean
    ) : PlayerState
}