package com.practicum.playlistmaker.presentation.widgets

import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.search.TrackAdapter

class StickyFooterDecoration : RecyclerView.ItemDecoration() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var stickyContainer: FrameLayout
    private lateinit var blurredView: BlurredImageView
    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener { updateFooterPosition() }
    private var isAttached = false

    fun attachRecyclerView(
        recyclerView: RecyclerView,
        adapter: TrackAdapter,
    ) {
        if (isAttached) return

        isAttached = true
        this.recycler = recyclerView
        this.adapter = adapter

        val rootView = (recyclerView.parent as ViewGroup)
        stickyContainer = rootView.findViewById(R.id.sticky_container)
        blurredView = rootView.findViewById(R.id.blur_image_view_footer)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    private fun updateFooterVisibility(isVisible: Boolean, doOnEnd: () -> Unit = {}) {
        recycler.post {
            blurredView.isVisible = !isVisible
            adapter.setFooterVisibility(adapter.itemCount - 1, isVisible)
            doOnEnd()
        }
    }

    private fun showFooter() {
        val position = adapter.itemCount - 1

        if (stickyContainer.childCount == 0) {
            val view = adapter.createViewHolder(stickyContainer, adapter.getItemViewType(position)).itemView

            updateFooterVisibility(false) {
                view.findViewById<Button>(R.id.btn_clear_history)?.setOnClickListener { adapter.getOnClearHistoryClickListener() }
                stickyContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun hideFooter() {
        blurredView.isVisible = false

        if (stickyContainer.childCount > 0) {
            updateFooterVisibility(true) {
                stickyContainer.removeAllViews()
            }
        }
    }

    private fun updateFooterPosition() {
        val layoutManager = recycler.layoutManager as? LinearLayoutManager
        val firstVisibleItemPosition = layoutManager?.findFirstCompletelyVisibleItemPosition() ?: RecyclerView.NO_POSITION
        val lastVisibleItemPosition = layoutManager?.findLastCompletelyVisibleItemPosition() ?: RecyclerView.NO_POSITION
        val totalItemCount = layoutManager?.itemCount ?: 0

        if (totalItemCount - 1 > lastVisibleItemPosition || firstVisibleItemPosition != 0) {
            showFooter()
        } else {
            hideFooter()
        }
    }

    fun detach() {
        if (!isAttached) return

        isAttached = false
        recycler.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        hideFooter()
    }
}