package com.practicum.playlistmaker.main.domain.api

interface InternetConnectionRepository {
    fun register()
    fun unregister()
    fun addOnInternetConnectListener(listener: InternetConnectListener)
    fun removeOnInternetConnectListener(listener: InternetConnectListener)
}