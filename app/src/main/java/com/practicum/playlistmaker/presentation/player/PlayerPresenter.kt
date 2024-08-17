package com.practicum.playlistmaker.presentation.player

import android.content.Context
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.Util.millisToSeconds
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.MediaPlayerListener
import com.practicum.playlistmaker.presentation.utils.Debounce

class PlayerPresenter(
    private val context: Context,
) {

    private val playerUI = context as PlayerUI
    private val playerRepository = Creator.getPlayerRepository(context)
    private val timer: Debounce by lazy {
        Debounce { updateProgress() }
    }

    fun updateTimer(isPlaying: Boolean) {
        if (!isPlaying && timer.isRunning) {
            timer.stop()
        } else if (isPlaying) {
            timer.start(true)
        }
    }

    fun init(stateListener: MediaPlayerListener, track: Track) {
        playerRepository.init(stateListener, track)
    }

    fun updateProgress() {
        playerUI.setProgress(
            if (playerRepository.isPlaying) playerRepository.currentPosition.millisToSeconds()
            else context.getString(R.string.default_duration_start)
        )
    }

    fun controlPlayback() {
        playerRepository.apply {
            if (isPlaying) pause() else play()
            playerUI.updatePlayBtn(isPlaying)
        }
    }

    fun release() {
        updateTimer(playerRepository.isPlaying)
        playerRepository.release()
    }
}
