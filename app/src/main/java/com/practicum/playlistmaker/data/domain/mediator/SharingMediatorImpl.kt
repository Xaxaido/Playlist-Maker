package com.practicum.playlistmaker.data.domain.mediator

import com.practicum.playlistmaker.settings.ExternalNavigator

class SharingMediatorImpl (
    private val externalNavigator: ExternalNavigator
) : SharingMediator {

    override fun shareApp() { externalNavigator.shareApp() }
    override fun contactSupport() { externalNavigator.contactSupport() }
    override fun userAgreement() { externalNavigator.userAgreement() }
}