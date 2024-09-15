package com.practicum.playlistmaker.main.domain.api

interface InternetConnectionCallback {
    fun onConnected()
    fun onDisconnected()
}