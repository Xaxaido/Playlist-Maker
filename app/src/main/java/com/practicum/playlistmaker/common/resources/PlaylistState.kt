package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

interface PlaylistState {

    object Default : PlaylistState
    class PlaylistInfo(
        val playlist: Playlist,
        val duration: Long,
    ) : PlaylistState
}