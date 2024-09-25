package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.model.Track
import javax.inject.Inject

class PlayerInteractorImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
): PlayerInteractor {

    override val isPlaying: Boolean get() = playerRepository.isPlaying
    override val currentPosition: Long get() = playerRepository.currentPosition
    override val bufferedProgress: Int get() = playerRepository.bufferedProgress

    override fun jsonToTrack(json: String) = playerRepository.jsonToTrack(json)

    override fun init(stateListener: MediaPlayerListener, track: Track) {
        playerRepository.init(stateListener, track)
    }

    override fun play() { playerRepository.play() }
    override fun pause() { playerRepository.pause() }
    override fun release() { playerRepository.release() }
}