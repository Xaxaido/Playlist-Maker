package com.practicum.playlistmaker.search.ui

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Util.dpToPx
import com.practicum.playlistmaker.player.domain.model.Track
import com.practicum.playlistmaker.databinding.ItemTrackBinding

class TrackViewHolder(
    private val binding: ItemTrackBinding,
    private var onTrackClick: (Track) -> Unit = {},
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
            .centerCrop()
            .transform(RoundedCorners(2.dpToPx(itemView.context)))
            .into(binding.cover)

        binding.trackTitle.text = track.trackName
        binding.artistName.setText(track.artistName, track.trackTimeMillis)
        itemView.setOnClickListener { onTrackClick(track) }
    }
}