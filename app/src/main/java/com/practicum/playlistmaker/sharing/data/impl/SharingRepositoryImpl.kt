package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import com.practicum.playlistmaker.sharing.domain.model.ActionType
import com.practicum.playlistmaker.sharing.domain.model.IntentAction

class SharingRepositoryImpl(
    private val context: Context,
) : SharingRepository {

    override fun getShareApp() = IntentAction(
        ActionType.SHARE_APP,
        mapOf(IntentAction.CONTENT to context.getString(R.string.share_app_content))
    )

    override fun getContactSupport() = IntentAction(
        ActionType.CONTACT_SUPPORT,
        mapOf(
            IntentAction.EMAIL to context.getString(R.string.contact_support_email),
            IntentAction.SUBJECT to buildString {
                append("${context.getString(R.string.contact_support_subject)} ")
                append(context.getString(R.string.app_name))
            },
            IntentAction.CONTENT to context.getString(R.string.contact_support_text),
        )
    )

    override fun getOpenTerms() = IntentAction(
        ActionType.OPEN_TERMS,
        mapOf(IntentAction.CONTENT to context.getString(R.string.user_agreement_content))
    )
}