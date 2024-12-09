package com.practicum.playlistmaker.medialibrary.ui.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PlaylistItemDecoration(
    private val spanCount: Int,
    private val edgeSpacing: Int,
    private val bottomSpacing: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = if (column == 0) edgeSpacing else 0
        outRect.right = if (column == spanCount - 1) edgeSpacing else 0
        outRect.bottom = bottomSpacing
    }
}


