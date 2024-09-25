package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface PlayerRepository {
    val isPlaying: Boolean
    val currentPosition: Long
    val bufferedProgress: Int
    fun jsonToTrack(json: String): Track
    fun init(stateListener: MediaPlayerListener, track: Track)
    fun play()
    fun pause()
    fun release()
}