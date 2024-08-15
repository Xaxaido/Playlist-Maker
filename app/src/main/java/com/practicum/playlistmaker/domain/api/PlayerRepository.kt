package com.practicum.playlistmaker.domain.api

import androidx.media3.common.Player
import com.practicum.playlistmaker.domain.models.Track

interface MediaRepository {
    val isPlaying: Boolean
    val currentPosition: Long
    fun init(stateListener: Player.Listener, track: Track)
    fun play()
    fun pause()
    fun release()
}