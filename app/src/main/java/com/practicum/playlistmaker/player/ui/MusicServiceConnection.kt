package com.practicum.playlistmaker.player.ui

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.practicum.playlistmaker.player.services.MusicService
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel

class MusicServiceConnection(private val viewModel: PlayerViewModel) : ServiceConnection {

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MusicServiceBinder
        viewModel.setAudioPlayerControl(binder.getService())
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        viewModel.removeAudioPlayerControl()
    }
}