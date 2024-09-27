package com.practicum.playlistmaker.main.domain.api

interface InternetConnectListener {
    fun onConnectionStatusUpdate(hasInternet: Boolean)
}