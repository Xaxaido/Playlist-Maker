package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.TrackDescription
import com.practicum.playlistmaker.search.domain.model.Track

interface PlayerState {

    object Default : PlayerState
    object Stop : PlayerState
    class IsPlaying(
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
        val isFavorite: Boolean,
        val shouldPlayAnimation: Boolean = true
    ) : PlayerState
    class AddToPlaylist(
        val playlistTitle: String,
        val isAdded: Boolean
    ) : PlayerState
}