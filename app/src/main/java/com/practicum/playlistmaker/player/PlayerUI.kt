package com.practicum.playlistmaker.player

interface PlayerUI {
    fun updatePlayBtn(isPlaying: Boolean)
    fun setProgress(progress: String)
}