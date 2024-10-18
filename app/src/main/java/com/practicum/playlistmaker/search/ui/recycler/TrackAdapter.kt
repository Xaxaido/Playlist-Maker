package com.practicum.playlistmaker.search.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.TrackListItem

class TrackAdapter(
    onTrackClick: (Track) -> Unit = {},
    onClearHistoryClick: () -> Unit = {},
): AsyncListDifferDelegationAdapter<TrackListItem>(diffCallback) {

    init {
        delegatesManager
            .addDelegate(headerItemDelegate())
            .addDelegate(trackItemDelegate(onTrackClick))
            .addDelegate(footerItemDelegate(onClearHistoryClick))
    }

    fun getItem(pos: Int): TrackListItem = differ.currentList[pos]

    fun submitTracksList(
        isDecorationNeeded: Boolean = false,
        list: List<Track>,
        isDataSetChanged: Boolean = false,
        doOnEnd: (() -> Unit) = {},
    ) {
        val items = convertToTrackListItem(isDecorationNeeded, list)
        if (isDataSetChanged) differ.submitList(null)
        differ.submitList(items) {
            doOnEnd()
        }
    }

    fun updateFooterVisibility(isVisible: Boolean) {
        val position = itemCount - 1
        if (position == RecyclerView.NO_POSITION) return
        differ.currentList[position].also {
            if (it is TrackListItem.Footer) {
                it.isVisible  = isVisible
                notifyItemChanged(position)
            }
        }
    }

    private fun convertToTrackListItem(
        isDecorationNeeded: Boolean = false,
        list: List<Track>,
    ): List<TrackListItem> {
        return buildList {
            if (isDecorationNeeded) this += TrackListItem.Header
            this += list.map { TrackListItem.TrackItem(it) }
            if (isDecorationNeeded) this += TrackListItem.Footer()
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
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: TrackListItem, newItem: TrackListItem): Boolean {
                return when {
                    oldItem is TrackListItem.TrackItem && newItem is TrackListItem.TrackItem -> {
                        oldItem.track == newItem.track
                    }
                    oldItem is TrackListItem.Header && newItem is TrackListItem.Header -> true
                    oldItem is TrackListItem.Footer && newItem is TrackListItem.Footer -> oldItem.isVisible == newItem.isVisible
                    else -> false
                }
            }
        }
    }
}
