package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.search.domain.model.Track

interface FavoriteTracksState {

    object Loading : FavoriteTracksState
    object Empty : FavoriteTracksState
    class Content(
        val tracks: List<Track>
    ) : FavoriteTracksState
}