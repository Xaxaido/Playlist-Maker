package com.practicum.playlistmaker.main.domain.api

interface InternetConnectionInteractor {
    fun register()
    fun unregister()
    fun addOnInternetConnectListener(callback: InternetConnectListener)
    fun removeOnInternetConnectListener(callback: InternetConnectListener)
}