package com.practicum.playlistmaker.medialibrary.ui.recycler

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemBottomSheetBinding
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistViewHolder(
    private val binding: ViewBinding,
    private val onClick: (Playlist) -> Unit = {},
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        when (binding) {
            is ItemPlaylistBinding -> {
                Glide.with(itemView)
                    .load(playlist.cover)
                    .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
                    .centerCrop()
                    .into(binding.cover)

                binding.title.text = playlist.name
                binding.tracksCount.text = playlist.tracksCount.toString()
            }
            is ItemBottomSheetBinding -> {
                Glide.with(itemView)
                    .load(playlist.cover)
                    .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
                    .centerCrop()
                    .into(binding.cover)

                binding.title.text = playlist.name
                binding.tracksCount.text = playlist.tracksCount.toString()
            }
            else -> throw IllegalArgumentException("Unknown binding: ${binding::class}")
        }

        itemView.setOnClickListener { onClick(playlist) }
    }
}