package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.data.api.SharingRepository

class SharingInteractorImpl (
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override fun shareApp() { sharingRepository.shareApp() }
    override fun contactSupport() { sharingRepository.contactSupport() }
    override fun openTerms() { sharingRepository.openTerms() }
}