package com.practicum.playlistmaker.common.resources

sealed interface InternetState {

    data object Connected: InternetState
    data object ConnectionLost: InternetState
}