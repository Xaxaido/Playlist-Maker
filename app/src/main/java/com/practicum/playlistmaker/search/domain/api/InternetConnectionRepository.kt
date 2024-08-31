package com.practicum.playlistmaker.search.domain.api

import androidx.lifecycle.LiveData

interface InternetConnectionRepository {
    val internetStatus: LiveData<Boolean>
    fun register()
    fun unregister()
}