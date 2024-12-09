package com.practicum.playlistmaker.medialibrary.domain.model

sealed class PlaylistListItem {
    data class PlaylistItem(val playlist: Playlist) : PlaylistListItem()
    data class PlaylistBottomSheetItem(val playlist: Playlist) : PlaylistListItem()
}