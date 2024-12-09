package com.practicum.playlistmaker.medialibrary.ui.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemBottomSheetBinding
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistListItem

fun playlistItemDelegate(
    onClick: (Playlist) -> Unit = {},
) = adapterDelegateViewBinding<PlaylistListItem.PlaylistItem, PlaylistListItem, ItemPlaylistBinding>(
    { layoutInflater, root -> ItemPlaylistBinding.inflate(layoutInflater, root, false) }
) {
    bind {
        bindPlaylist(
            itemView,
            binding.cover,
            binding.title,
            binding.tracksCount,
            item.playlist,
            onClick,
        )
    }
}

fun playlistBottomSheetItemDelegate(
    onClick: (Playlist) -> Unit = {},
) = adapterDelegateViewBinding<PlaylistListItem.PlaylistBottomSheetItem, PlaylistListItem, ItemBottomSheetBinding>(
    { layoutInflater, root -> ItemBottomSheetBinding.inflate(layoutInflater, root, false) }
) {
    bind {
        bindPlaylist(
            itemView,
            binding.cover,
            binding.title,
            binding.tracksCount,
            item.playlist,
            onClick,
        )
    }
}

private fun bindPlaylist(
    itemView: View,
    cover: ImageView,
    title: TextView,
    tracksCount: TextView,
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
) {
    Glide.with(itemView)
        .load(playlist.cover)
        .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
        .centerCrop()
        .into(cover)

    title.text = playlist.name
    tracksCount.text = playlist.tracksCount.toString()
    itemView.setOnClickListener { onClick(playlist) }
}