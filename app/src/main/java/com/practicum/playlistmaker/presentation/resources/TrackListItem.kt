package com.practicum.playlistmaker.presentation.resources

import com.practicum.playlistmaker.domain.models.Track

sealed class TrackListItem {
    data class TrackItem(val track: Track) : TrackListItem()
    data class Footer(var isVisible: Boolean = true) : TrackListItem()
    data object Header : TrackListItem()
}