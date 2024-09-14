package com.practicum.playlistmaker.sharing.data.api

import com.practicum.playlistmaker.sharing.data.model.ShareAction

interface SharingRepository {
    fun getShareApp(): ShareAction
    fun getContactSupport(): ShareAction
    fun getOpenTerms(): ShareAction
}