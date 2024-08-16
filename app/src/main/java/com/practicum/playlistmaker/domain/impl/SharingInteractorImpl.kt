package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingInteractor
import com.practicum.playlistmaker.domain.api.ExternalNavigator

class SharingInteractorImpl (
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() { externalNavigator.shareApp() }
    override fun contactSupport() { externalNavigator.contactSupport() }
    override fun userAgreement() { externalNavigator.userAgreement() }
}