package com.practicum.playlistmaker.search.domain.impl

import androidx.lifecycle.LiveData
import com.practicum.playlistmaker.search.domain.api.InternetConnectionInteractor
import com.practicum.playlistmaker.search.domain.api.InternetConnectionRepository

class InternetConnectionInteractorImpl(
    private val internetConnectionRepository: InternetConnectionRepository
) : InternetConnectionInteractor {

    override val internetStatus: LiveData<Boolean> = internetConnectionRepository.internetStatus

    override fun register() { internetConnectionRepository.register() }
    override fun unregister() { internetConnectionRepository.unregister() }
}
