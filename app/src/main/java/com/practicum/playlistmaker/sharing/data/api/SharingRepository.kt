package com.practicum.playlistmaker.sharing.data.api

import com.practicum.playlistmaker.sharing.domain.model.IntentAction

interface SharingRepository {
    fun getShareApp(): IntentAction
    fun getContactSupport(): IntentAction
    fun getOpenTerms(): IntentAction
}