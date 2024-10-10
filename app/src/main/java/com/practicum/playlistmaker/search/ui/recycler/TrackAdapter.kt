package com.practicum.playlistmaker.search.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.databinding.ItemFooterBinding
import com.practicum.playlistmaker.databinding.ItemHeaderBinding
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.search.domain.model.TrackListItem

class TrackAdapter(
    private val recycler: RecyclerView? = null,
) : ListAdapter<TrackListItem, RecyclerView.ViewHolder>(diffCallback) {

    private var onTrackClick: (Track) -> Unit = {}
    private var onClearHistoryClick: () -> Unit = {}
    private var isFooterVisible = true

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
        doOnEnd: ((Boolean) -> Unit) = {},
    ) {
        val items = convertToTrackListItem(isDecorationNeeded, list)
        if (isDataSetChanged) submitList(null)
        submitList(items) {
            doOnEnd(updateBtnPosition())
        }
    }

    private fun updateFooterVisibility(isVisible: Boolean) {
        val position = itemCount - 1
        if (position == RecyclerView.NO_POSITION || isFooterVisible == isVisible) return
        getItem(position).also {
            if (it is TrackListItem.Footer) {
                isFooterVisible = isVisible
                notifyItemChanged(position)
            }
        }
    }

    private fun updateBtnPosition(): Boolean {
        val layoutManager = recycler?.layoutManager as? LinearLayoutManager
        val firstVisibleItemPosition = layoutManager?.findFirstCompletelyVisibleItemPosition() ?: RecyclerView.NO_POSITION
        val lastVisibleItemPosition = layoutManager?.findLastCompletelyVisibleItemPosition() ?: RecyclerView.NO_POSITION
        val isButtonVisible = itemCount > lastVisibleItemPosition || firstVisibleItemPosition > 0

        updateFooterVisibility(!isButtonVisible)
        return isButtonVisible
    }

    private fun convertToTrackListItem(
        isDecorationNeeded: Boolean = false,
        list: List<Track>,
    ): List<TrackListItem> {
        return buildList<TrackListItem> {
            if (isDecorationNeeded) this += TrackListItem.Header
            this += list.map { TrackListItem.TrackItem(it) }
            if (isDecorationNeeded)  this += TrackListItem.Footer
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TrackListItem.TrackItem -> R.layout.item_track
            is TrackListItem.Header -> R.layout.item_header
            is TrackListItem.Footer -> R.layout.item_footer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_track-> {
                val binding = ItemTrackBinding.inflate(inflater, parent, false)
                TrackViewHolder(binding, onTrackClick)
            }
            R.layout.item_header-> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            R.layout.item_footer -> {
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
            is TrackListItem.Footer -> (holder as FooterViewHolder).bind(isFooterVisible)
        }
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
