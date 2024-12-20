package com.practicum.playlistmaker.search.ui.recycler

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.utils.Extensions.millisToSeconds
import com.practicum.playlistmaker.databinding.ItemFooterBinding
import com.practicum.playlistmaker.databinding.ItemHeaderBinding
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.TrackListItem

fun headerItemDelegate() =
    adapterDelegateViewBinding<TrackListItem.Header, TrackListItem, ItemHeaderBinding>(
        { layoutInflater, root -> ItemHeaderBinding.inflate(layoutInflater, root, false) }
    ) {
        bind {}
    }

fun trackItemDelegate(
    onTrackClick: (Track) -> Unit,
    onLongTrackClick: (Int, Track) -> Boolean = { _, _ -> false },
    showFavorites: Boolean,
) = adapterDelegateViewBinding<TrackListItem.TrackItem, TrackListItem, ItemTrackBinding>(
    { layoutInflater, root -> ItemTrackBinding.inflate(layoutInflater, root, false) }
) {
    bind {
        val track = item.track
        Glide.with(itemView)
            .load(track.albumCover)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
            .centerCrop()
            .transform(RoundedCorners(2.dpToPx(itemView.context)))
            .into(binding.cover)

        if (showFavorites) binding.favorite.isVisible = track.isFavorite
        binding.trackTitle.text = track.trackName
        binding.artistName.setText(track.artistName, track.duration.millisToSeconds())
        itemView.setOnClickListener { onTrackClick(track) }
        itemView.setOnLongClickListener { onLongTrackClick(absoluteAdapterPosition, track) }
        itemView.isVisible = true
    }
}

fun footerItemDelegate(
    onClearHistoryBtnClick: () -> Unit = {},
) = adapterDelegateViewBinding<TrackListItem.Footer, TrackListItem, ItemFooterBinding>(
    { layoutInflater, root -> ItemFooterBinding.inflate(layoutInflater, root, false) }
) {
    bind {
        binding.clearHistory.isVisible = item.isVisible
        binding.btnClearHistory.setOnClickListener { onClearHistoryBtnClick() }
    }
}