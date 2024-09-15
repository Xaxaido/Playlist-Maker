package com.practicum.playlistmaker.main.domain.api

interface InternetConnectionInteractor {
    fun register()
    fun unregister()
    fun setCallback(callback: InternetConnectionCallback)
}