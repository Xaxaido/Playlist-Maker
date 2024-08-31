package com.practicum.playlistmaker.search.domain.api

import androidx.lifecycle.LiveData

interface InternetConnectionInteractor {
    val internetStatus: LiveData<Boolean>
    fun register()
    fun unregister()
}