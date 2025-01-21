package com.practicum.playlistmaker.player.services

import com.practicum.playlistmaker.common.resources.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    val isPLaying: Boolean
    val playerState: StateFlow<PlayerState>
    val trackBufferingState: StateFlow<Int>
    fun play()
    fun pause()
    fun startForeground()
    fun stopForeground()
}