package com.practicum.playlistmaker.data.domain.repository

import androidx.media3.common.Player
import com.practicum.playlistmaker.data.domain.model.Track

interface MediaRepository {
    val isPlaying: Boolean
    val currentPosition: Long
    fun init(stateListener: Player.Listener, track: Track)
    fun play()
    fun pause()
    fun release()
}