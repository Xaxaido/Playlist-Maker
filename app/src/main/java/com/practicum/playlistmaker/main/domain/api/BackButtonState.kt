package com.practicum.playlistmaker.main.domain.api

interface BackButtonState {
    var customNavigateAction: (() -> Boolean)?
    fun updateBackBtn(enabled: Boolean)
    fun setCustomNavigation(action: () -> Boolean)
    fun setIconColor(isDefault: Boolean)
    fun setTitle(title: String)
}