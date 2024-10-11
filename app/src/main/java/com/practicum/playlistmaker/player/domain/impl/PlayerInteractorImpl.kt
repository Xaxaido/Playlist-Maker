package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.model.Track

class PlayerInteractorImpl(
    private val playerRepository: PlayerRepository,
): PlayerInteractor {

    override val isPlaying get() = playerRepository.isPlaying
    override val currentPosition get() = playerRepository.currentPosition
    override val bufferedProgress get() = playerRepository.bufferedProgress

    override fun jsonToTrack(json: String) = playerRepository.jsonToTrack(json)

    override fun init(stateListener: MediaPlayerListener, track: Track) {
        playerRepository.init(stateListener, track)
    }

    override fun play() { playerRepository.play() }
    override fun pause() { playerRepository.pause() }
    override fun release() { playerRepository.release() }
}