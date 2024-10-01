package com.practicum.playlistmaker.search.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.TrackListItem
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

    fun submitTracksList(
        isDecorationNeeded: Boolean = false,
        list: List<Track>,
        isDataSetChanged: Boolean = false,
        doOnEnd: (() -> Unit) = {},
    ) {
        val items = convertToTrackListItem(isDecorationNeeded, list)
        if (isDataSetChanged) submitList(null)
        submitList(items, doOnEnd)
    }

    fun setFooterVisibility(position: Int, isVisible: Boolean) {
        if (position < 0) return

        (getItem(position) as TrackListItem.Footer).isVisible = isVisible
        notifyItemChanged(position)
    }

    private fun convertToTrackListItem(
        isDecorationNeeded: Boolean = false,
        list: List<Track>,
    ): List<TrackListItem> {
        val items = mutableListOf<TrackListItem>()

        if (isDecorationNeeded) items.add(TrackListItem.Header)
        items.addAll(list.map { TrackListItem.TrackItem(it) })
        if (isDecorationNeeded) items.add(TrackListItem.Footer())

        return items
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TrackListItem.TrackItem -> ViewType.TRACK.type
            is TrackListItem.Header -> ViewType.HEADER.type
            is TrackListItem.Footer -> ViewType.FOOTER.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.TRACK.type -> {
                val binding = ItemTrackBinding.inflate(inflater, parent, false)
                TrackViewHolder(binding, onTrackClick)
            }
            ViewType.HEADER.type -> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            ViewType.FOOTER.type -> {
                val binding = ItemFooterBinding.inflate(inflater, parent, false)
                FooterViewHolder(binding, onClearHistoryClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TrackListItem.TrackItem -> {
                (holder as TrackViewHolder).bind(item.track)
            }
            is TrackListItem.Header -> (holder as HeaderViewHolder).bind()
            is TrackListItem.Footer -> (holder as FooterViewHolder).bind(item.isVisible)
        }
    }

    private enum class ViewType(
        val type: Int,
    ) {

        HEADER(0),
        TRACK(1),
        FOOTER(2),
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<TrackListItem>() {

            override fun areItemsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
                return when {
                    oldItem is TrackListItem.TrackItem &&newItem is TrackListItem.TrackItem -> {
                        oldItem.track.id == newItem.track.id
                    }
                    oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
                    oldItem is TrackListItem.Footer && newItem is TrackListItem.Footer -> true
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
    }
}
