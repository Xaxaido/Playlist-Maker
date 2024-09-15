package com.practicum.playlistmaker.main.domain.api

interface InternetConnectionRepository {
    fun register()
    fun unregister()
    fun setCallback(callback: InternetConnectionCallback)
}