package com.practicum.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackListItem
import com.practicum.playlistmaker.databinding.ItemFooterBinding
import com.practicum.playlistmaker.databinding.ItemHeaderBinding
import com.practicum.playlistmaker.databinding.ItemTrackBinding

class TrackAdapter : ListAdapter<TrackListItem, RecyclerView.ViewHolder>(diffCallback) {

    private var onTrackClick: (Track) -> Unit = {}
    private var onClearHistoryClick: () -> Unit = {}

    fun getOnClearHistoryClickListener() = onClearHistoryClick()

    fun setOnTrackClickListener(onClickListener: (Track) -> Unit) {
        onTrackClick = onClickListener
    }

    fun setOnClearHistoryClick(onClickListener: () -> Unit) {
        onClearHistoryClick = onClickListener
    }

    fun submitTracksList(isDecorationNeeded: Boolean = false, list: List<Track>, onFinish: (() -> Unit) = {}) {
        val items = mutableListOf<TrackListItem>()

        if (isDecorationNeeded) items.add(TrackListItem.Header)
        items.addAll(list.map { TrackListItem.TrackItem(it) })
        if (isDecorationNeeded) items.add(TrackListItem.Footer())

        submitList(items, onFinish)
    }

    fun setFooterVisibility(position: Int, isVisible: Boolean) {
        (getItem(position) as TrackListItem.Footer).isVisible = isVisible
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TrackListItem.TrackItem -> VIEW_TYPE_ITEM
            is TrackListItem.Header -> VIEW_TYPE_HEADER
            is TrackListItem.Footer -> VIEW_TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = ItemTrackBinding.inflate(inflater, parent, false)
                TrackViewHolder(binding, onTrackClick)
            }
            VIEW_TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_FOOTER -> {
                val binding = ItemFooterBinding.inflate(inflater, parent, false)
                FooterViewHolder(binding, onClearHistoryClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TrackListItem.TrackItem -> (holder as TrackViewHolder).bind(item.track)
            is TrackListItem.Header -> (holder as HeaderViewHolder).bind()
            is TrackListItem.Footer -> (holder as FooterViewHolder).bind(item.isVisible)
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_FOOTER = 2

        private val diffCallback = object : DiffUtil.ItemCallback<TrackListItem>() {

            override fun areItemsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
                return when {
                    oldItem is TrackListItem.TrackItem && newItem is TrackListItem.TrackItem -> oldItem.track.trackId == newItem.track.trackId
                    oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
                    oldItem is TrackListItem.Footer && newItem is TrackListItem.Footer -> true
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
                return when {
                    oldItem is TrackListItem.TrackItem && newItem is TrackListItem.TrackItem -> oldItem.track == newItem.track
                    oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
                    oldItem is TrackListItem.Footer && newItem is TrackListItem.Footer -> oldItem.isVisible == newItem.isVisible
                    else -> false
                }
            }
        }
    }
}
