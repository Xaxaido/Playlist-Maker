package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.api.SharingRepository

class SharingInteractorImpl (
    private val externalNavigator: SharingRepository
) : SharingInteractor {

    override fun shareApp() { externalNavigator.shareApp() }
    override fun contactSupport() { externalNavigator.contactSupport() }
    override fun userAgreement() { externalNavigator.userAgreement() }
}