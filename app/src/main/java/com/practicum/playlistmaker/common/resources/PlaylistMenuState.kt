package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

interface PlaylistMenuState {

    object Remove : PlaylistMenuState
    class Share(
        val playlist: Playlist,
        val tracks: List<Track>,
    ) : PlaylistMenuState
}