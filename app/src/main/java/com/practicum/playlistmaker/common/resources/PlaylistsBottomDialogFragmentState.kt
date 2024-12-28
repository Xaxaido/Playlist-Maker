package com.practicum.playlistmaker.common.resources

interface PlaylistsBottomDialogFragmentState {

    object Default : PlaylistsBottomDialogFragmentState
    class AddToPlaylist(
        val playlistTitle: String,
        val isAdded: Boolean
    ) : PlaylistsBottomDialogFragmentState
}