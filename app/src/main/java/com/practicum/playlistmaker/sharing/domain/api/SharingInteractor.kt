package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.data.model.ShareAction

interface SharingInteractor {
    fun getShareApp(): ShareAction
    fun getContactSupport(): ShareAction
    fun getOpenTerms(): ShareAction
}