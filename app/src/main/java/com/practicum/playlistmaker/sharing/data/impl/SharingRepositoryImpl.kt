package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.api.SharingRepository
import com.practicum.playlistmaker.sharing.data.model.ActionType
import com.practicum.playlistmaker.sharing.data.model.ShareAction

class SharingRepositoryImpl(
    private val context: Context,
) : SharingRepository {

    override fun getShareApp() = ShareAction(
        ActionType.SHARE_APP,
        mapOf(ShareAction.CONTENT to context.getString(R.string.share_app_content))
    )

    override fun getContactSupport() = ShareAction(
        ActionType.CONTACT_SUPPORT,
        mapOf(
            ShareAction.EMAIL to context.getString(R.string.contact_support_email),
            ShareAction.SUBJECT to buildString {
                append("${context.getString(R.string.contact_support_subject)} ")
                append(context.getString(R.string.app_name))
            },
            ShareAction.CONTENT to context.getString(R.string.contact_support_text),
        )
    )

    override fun getOpenTerms() = ShareAction(
        ActionType.OPEN_TERMS,
        mapOf(ShareAction.CONTENT to context.getString(R.string.user_agreement_content))
    )
}