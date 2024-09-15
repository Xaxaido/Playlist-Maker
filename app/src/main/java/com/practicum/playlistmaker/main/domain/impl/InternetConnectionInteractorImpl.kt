package com.practicum.playlistmaker.main.domain.impl

import com.practicum.playlistmaker.main.domain.api.InternetConnectionCallback
import com.practicum.playlistmaker.main.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository

class InternetConnectionInteractorImpl(
    private val internetConnectionRepository: InternetConnectionRepository,
) : InternetConnectionInteractor {

    override fun register() { internetConnectionRepository.register() }
    override fun unregister() { internetConnectionRepository.unregister() }
    override fun setCallback(callback: InternetConnectionCallback) {
        internetConnectionRepository.setCallback(callback)
    }
}
