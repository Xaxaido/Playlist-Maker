package com.practicum.playlistmaker.data.source

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.entity.Track
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds

class TrackAdapter : ListAdapter<Track, TrackAdapter.TrackViewHolder>(diffCallback) {

    private val mDiffer = AsyncListDiffer(this, diffCallback)
    private var onClick: (Track) -> Unit = {}

    fun setOnClickListener(onClickListener: (Track) -> Unit) {
        onClick = onClickListener
    }

    fun submitTracksList(list: List<Track>, onFinish: (() -> Unit) = {}) {
        mDiffer.submitList(list, onFinish)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTrackBinding.inflate(inflater, parent, false)

        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = mDiffer.currentList.size

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pos: Int) {
            val track = mDiffer.currentList[pos]

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.album_cover_stub))
                .centerCrop()
                .transform(RoundedCorners(2.dpToPx(itemView.context.applicationContext)))
                .into(binding.cover)

            itemView.setOnClickListener {
                onClick(track)
            }

            binding.trackTitle.text = track.trackName
            binding.artistName.setText(track.artistName, track.trackTimeMillis.millisToSeconds())

            itemView.setOnClickListener { onClick(track) }
        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Track>() {

            override fun areItemsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackId == newItem.trackId

            override fun areContentsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackName == newItem.trackName && oldItem.artistName == newItem.artistName
        }
    }
}