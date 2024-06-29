package com.practicum.playlistmaker.extension.state

import android.view.View
import androidx.core.view.isVisible

sealed interface VisibilityState {
    object ShowError : VisibilityState
    object ShowNothingFound : VisibilityState
    object ShowNoData : VisibilityState
    object ShowHistory : VisibilityState
    object ShowContent : VisibilityState
    object ShowProgress : VisibilityState
    class Views(
        private var list: Map<VisibilityState, View>
    ) {

        fun updateVisibility(state: VisibilityState) {
            for (view in list.entries) {
                view.value.isVisible = state::class == view.key::class
            }
        }
    }
}