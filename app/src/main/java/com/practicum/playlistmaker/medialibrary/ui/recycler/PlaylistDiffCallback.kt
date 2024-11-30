package com.practicum.playlistmaker.medialibrary.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {

    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}