package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

interface CreatePlaylistState {

    object Default : CreatePlaylistState
    class Create(
        val title: String,
    ) : CreatePlaylistState
    class Edit(
        val playlist: Playlist,
    ) : CreatePlaylistState
}