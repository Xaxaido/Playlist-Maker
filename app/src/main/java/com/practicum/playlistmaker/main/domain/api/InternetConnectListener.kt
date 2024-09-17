package com.practicum.playlistmaker.main.domain.api

interface InternetConnectListener {
    fun onConnected()
    fun onDisconnected()
}