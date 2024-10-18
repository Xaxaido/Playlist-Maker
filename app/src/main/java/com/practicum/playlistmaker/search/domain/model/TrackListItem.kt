package com.practicum.playlistmaker.search.domain.model

sealed class TrackListItem {
    data class TrackItem(val track: Track) : TrackListItem()
    data class Footer(var isVisible: Boolean = true) : TrackListItem()
    data object Header : TrackListItem()
}