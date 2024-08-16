package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {
    val isPlaying: Boolean
    val currentPosition: Long
    fun init(stateListener: MediaPlayerListener, track: Track)
    fun play()
    fun pause()
    fun release()
}