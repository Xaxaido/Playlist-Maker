package com.practicum.playlistmaker.presentation.player

interface PlayerUI {
    fun updatePlayBtn(isPlaying: Boolean)
    fun setProgress(progress: String)
}