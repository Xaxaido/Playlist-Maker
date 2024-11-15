package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

interface FavoriteTracksState {

    object Empty : FavoriteTracksState
    object Loading : FavoriteTracksState
    class Content(
        val tracks: List<Track>
    ) : FavoriteTracksState
}