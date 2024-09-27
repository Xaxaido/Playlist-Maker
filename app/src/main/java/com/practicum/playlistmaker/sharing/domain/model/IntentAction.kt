package com.practicum.playlistmaker.sharing.domain.model

class IntentAction(
    val actionType: ActionType,
    val extra: Map<String, String>,
) {

    companion object {
        const val SUBJECT = "SHARE_ACTION_SUBJECT"
        const val EMAIL = "SHARE_ACTION_EMAIL"
        const val CONTENT = "SHARE_ACTION_CONTENT"
    }
}