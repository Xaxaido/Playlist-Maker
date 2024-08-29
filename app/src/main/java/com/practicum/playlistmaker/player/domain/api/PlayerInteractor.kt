package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.model.Track

interface PlayerInteractor {
    val isPlaying: Boolean
    val currentPosition: Long
    fun init(stateListener: MediaPlayerListener, track: Track)
    fun play()
    fun pause()
    fun release()
}