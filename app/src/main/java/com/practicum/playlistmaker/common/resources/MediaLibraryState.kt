package com.practicum.playlistmaker.common.resources

interface MediaLibraryState {

    object Loading : MediaLibraryState
    object Empty : MediaLibraryState
    class Content<T>(
        val list: List<T>
    ) : MediaLibraryState
}