package com.practicum.playlistmaker.search.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.search.domain.model.TrackListItem

class DiffCallback : DiffUtil.ItemCallback<TrackListItem>() {

    override fun areItemsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
        return when {
            oldItem is TrackListItem.TrackItem &&newItem is TrackListItem.TrackItem -> {
                oldItem.track.id == newItem.track.id
            }
            oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
        return when {
            oldItem is TrackListItem.TrackItem && newItem is TrackListItem.TrackItem -> {
                oldItem.track == newItem.track
            }
            oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
            oldItem is TrackListItem.Footer && newItem is TrackListItem.Footer -> true
            else -> false
        }
    }
}