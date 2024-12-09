package com.practicum.playlistmaker.search.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.TrackListItem

class TrackAdapter(
    onTrackClick: (Track) -> Unit = {},
    onClearHistoryClick: () -> Unit = {},
    showFavorites: Boolean = true,
): AsyncListDifferDelegationAdapter<TrackListItem>(DiffCallback()) {

    init {
        delegatesManager.addDelegate(headerItemDelegate())
            .addDelegate(trackItemDelegate(onTrackClick, showFavorites))
            .addDelegate(footerItemDelegate(onClearHistoryClick))
    }

    fun getItem(pos: Int): Track? = (differ.currentList[pos] as? TrackListItem.TrackItem)?.track

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
            if (it is TrackListItem.Footer && it.isVisible != isVisible) {
                it.isVisible = isVisible
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
}
