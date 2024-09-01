package com.practicum.playlistmaker.common.resources

import com.practicum.playlistmaker.player.domain.model.Track

sealed class TrackListItem {
    data class TrackItem(val track: Track) : TrackListItem()
    data class Footer(var isVisible: Boolean = true) : TrackListItem()
    data object Header : TrackListItem()
}