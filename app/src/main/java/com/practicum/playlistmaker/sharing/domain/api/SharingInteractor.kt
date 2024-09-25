package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.model.IntentAction

interface SharingInteractor {
    fun getShareApp(): IntentAction
    fun getContactSupport(): IntentAction
    fun getOpenTerms(): IntentAction
}