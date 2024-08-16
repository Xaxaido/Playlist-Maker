package com.practicum.playlistmaker.domain.api

interface MediaPlayerListener {
    fun onPlaybackStateChanged(playbackState: Int)
    fun onIsPlayingChanged(isPlaying: Boolean)
}