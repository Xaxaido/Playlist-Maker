package com.practicum.playlistmaker.player.domain.api

interface MediaPlayerListener {
    fun onPlaybackStateChanged(playbackState: Int)
}