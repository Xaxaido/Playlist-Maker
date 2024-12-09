package com.practicum.playlistmaker.medialibrary.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem

class PlaylistDiffCallback : DiffUtil.ItemCallback<PlaylistListItem>() {

    override fun areItemsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
        return when {
            oldItem is PlaylistListItem.PlaylistItem && newItem is PlaylistListItem.PlaylistItem -> {
                oldItem.playlist.id == newItem.playlist.id
            }
            oldItem is PlaylistListItem.PlaylistBottomSheetItem && newItem is PlaylistListItem.PlaylistBottomSheetItem -> {
                oldItem.playlist.id == newItem.playlist.id
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: PlaylistListItem, newItem: PlaylistListItem): Boolean {
        return when {
            oldItem is PlaylistListItem.PlaylistItem && newItem is PlaylistListItem.PlaylistItem -> {
                oldItem.playlist == newItem.playlist
            }
            oldItem is PlaylistListItem.PlaylistBottomSheetItem && newItem is PlaylistListItem.PlaylistBottomSheetItem -> {
                oldItem.playlist == newItem.playlist
            }
            else -> false
        }
    }
}