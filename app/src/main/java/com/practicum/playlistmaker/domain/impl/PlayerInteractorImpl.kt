package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository,
): PlayerInteractor {

    override val isPlaying: Boolean get() = playerRepository.isPlaying
    override val currentPosition: Long get() = playerRepository.currentPosition

    override fun init(stateListener: MediaPlayerListener, track: Track) {
        playerRepository.init(stateListener, track)
    }

    override fun play() { playerRepository.play() }
    override fun pause() { playerRepository.pause() }
    override fun release() { playerRepository.release() }
}