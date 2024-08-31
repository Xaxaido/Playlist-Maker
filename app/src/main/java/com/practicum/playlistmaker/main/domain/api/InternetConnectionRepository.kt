package com.practicum.playlistmaker.main.domain.api

import androidx.lifecycle.LiveData

interface InternetConnectionRepository {
    val internetStatus: LiveData<Boolean>
    fun register()
    fun unregister()
}