package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SharingMediator
import com.practicum.playlistmaker.presentation.settings.ExternalNavigator

class SharingMediatorImpl (
    private val externalNavigator: ExternalNavigator
) : SharingMediator {

    override fun shareApp() { externalNavigator.shareApp() }
    override fun contactSupport() { externalNavigator.contactSupport() }
    override fun userAgreement() { externalNavigator.userAgreement() }
}