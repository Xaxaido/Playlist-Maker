package com.practicum.playlistmaker.common.resources

import android.view.View
import androidx.core.view.isVisible

sealed interface VisibilityState {
    data object Error : VisibilityState
    data object NothingFound : VisibilityState
    data object NoData : VisibilityState
    data object History : VisibilityState
    data object SearchResults : VisibilityState
    data object Loading : VisibilityState
    class VisibilityItem(
        val view: View,
        val type: List<VisibilityState>,
    )
    class ViewsList(
       val views: List<VisibilityItem>,
    ) {

        fun show(state: VisibilityState, views: List<VisibilityItem> = this.views) {
            views.forEach {
                it.view.isVisible = it.type.contains(state)
            }
        }
    }
}