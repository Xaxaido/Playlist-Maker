package com.practicum.playlistmaker.main.domain.impl

import com.practicum.playlistmaker.main.domain.api.InternetConnectListener
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository

class InternetConnectionInteractorImpl(
    private val internetConnectionRepository: InternetConnectionRepository,
) : InternetConnectionInteractor {

    override fun register() { internetConnectionRepository.register() }
    override fun unregister() { internetConnectionRepository.unregister() }

    override fun addOnInternetConnectListener(callback: InternetConnectListener) {
        internetConnectionRepository.addOnInternetConnectListener(callback)
    }

    override fun removeOnInternetConnectListener(callback: InternetConnectListener) {
        internetConnectionRepository.removeOnInternetConnectListener(callback)
    }
}
