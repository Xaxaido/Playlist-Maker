package com.practicum.playlistmaker.data.source

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.data.domain.model.Track
import com.practicum.playlistmaker.databinding.ItemTrackBinding

class TrackAdapter : ListAdapter<Track, TrackViewHolder>(diffCallback) {

    private val mDiffer = AsyncListDiffer(this, diffCallback)
    private var onTrackClick: (Track) -> Unit = {}

    fun setOnTrackClickListener(onClickListener: (Track) -> Unit) {
        onTrackClick = onClickListener
    }

    fun submitTracksList(list: List<Track>, onFinish: (() -> Unit) = {}) {
        mDiffer.submitList(list, onFinish)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTrackBinding.inflate(inflater, parent, false)

        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(mDiffer.currentList[position])
    }

    override fun getItemCount() = mDiffer.currentList.size

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Track>() {

            override fun areItemsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackId == newItem.trackId

            override fun areContentsTheSame(oldItem: Track, newItem: Track) =
                oldItem.trackName == newItem.trackName && oldItem.artistName == newItem.artistName
        }
    }
}