package com.practicum.playlistmaker.medialibrary.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemBottomSheetBinding
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistAdapter(
    private val layoutId: Int,
    private val onClick: (Playlist) -> Unit = {},
) : ListAdapter<Playlist, PlaylistViewHolder>(PlaylistDiffCallback()) {

    fun submitTracksList(list: List<Playlist>, doOnEnd: (() -> Unit) = {}) {
        submitList(list) {
            doOnEnd()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (layoutId) {
            R.layout.item_playlist -> ItemPlaylistBinding.inflate(inflater, parent, false)
            R.layout.item_bottom_sheet -> ItemBottomSheetBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown layoutId: $layoutId")
        }

        return PlaylistViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
