package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import javax.inject.Inject

class SharingInteractorImpl @Inject constructor(
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override fun getShareApp() = sharingRepository.getShareApp()
    override fun getContactSupport() = sharingRepository.getContactSupport()
    override fun getOpenTerms() = sharingRepository.getOpenTerms()
}