package com.practicum.playlistmaker.search.domain.model

sealed class TrackListItem {
    data class TrackItem(val track: Track) : TrackListItem()
    data object Footer : TrackListItem()
    data object Header : TrackListItem()
}