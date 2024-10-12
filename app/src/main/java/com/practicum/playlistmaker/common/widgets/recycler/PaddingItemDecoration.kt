package com.practicum.playlistmaker.common.widgets.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingItemDecoration(
    private val padding: IntArray,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == 0) {
            outRect.top = padding[0]
        }

        if (position == itemCount - 1) {
            outRect.bottom = padding[1]
        }
    }
}
