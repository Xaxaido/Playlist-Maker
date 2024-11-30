package com.practicum.playlistmaker.main.domain.api

interface BackButtonState {
    fun updateBackBtn(enabled: Boolean)
    fun setCustomNavigation(action: () -> Boolean)
}