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
    private val playerInteractor = Creator.getPlayerInteractor(context)
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
        playerInteractor.init(stateListener, track)
    }

    fun updateProgress() {
        playerUI.setProgress(
            if (playerInteractor.isPlaying) playerInteractor.currentPosition.millisToSeconds()
            else context.getString(R.string.default_duration_start)
        )
    }

    fun controlPlayback() {
        playerInteractor.apply {
            if (isPlaying) pause() else play()
            playerUI.updatePlayBtn(isPlaying)
        }
    }

    fun release() {
        updateTimer(playerInteractor.isPlaying)
        playerInteractor.release()
    }
}
