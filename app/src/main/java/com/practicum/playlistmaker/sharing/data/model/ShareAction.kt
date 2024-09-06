package com.practicum.playlistmaker.sharing.data.model

data class ShareAction(
    val actionType: ActionType,
    val email: String? = null,
    val subject: String? = null,
    val content: String? = null,
)
