package com.practicum.playlistmaker.player

import android.content.Context
import androidx.media3.common.Player
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.domain.model.Track
import com.practicum.playlistmaker.data.domain.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.extension.util.Debounce
import com.practicum.playlistmaker.extension.util.Util.millisToSeconds

class PlayerPresenter(
    private val context: Context,
) {

    private val playerUI = context as PlayerUI
    private val playerRepository = PlayerRepositoryImpl(context)
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

    fun init(stateListener: Player.Listener, track: Track) {
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
