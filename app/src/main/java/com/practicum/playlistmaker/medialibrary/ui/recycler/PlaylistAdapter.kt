package com.practicum.playlistmaker.medialibrary.ui.recycler

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem

class PlaylistAdapter(
    onClick: (Playlist) -> Unit = {},
) : AsyncListDifferDelegationAdapter<PlaylistListItem>(PlaylistDiffCallback()) {

    init {
        delegatesManager
            .addDelegate(playlistItemDelegate(onClick))
            .addDelegate(playlistBottomSheetItemDelegate(onClick))
    }

    fun submitTracksList(list: List<PlaylistListItem>, doOnEnd: (() -> Unit) = {}) {
        differ.submitList(list) {
            doOnEnd()
        }
    }

    fun <T> convertToPlaylistListItem(
        clazz: Class<T>,
        list: List<Playlist>,
    ): List<PlaylistListItem> {
        return buildList {
            this += when (clazz) {
                PlaylistListItem.PlaylistItem::class.java -> {
                    list.map { PlaylistListItem.PlaylistItem(it) }
                }
                PlaylistListItem.PlaylistBottomSheetItem::class.java -> {
                    list.map { PlaylistListItem.PlaylistBottomSheetItem(it) }
                }
                else -> emptyList()
            }
        }
    }
}
