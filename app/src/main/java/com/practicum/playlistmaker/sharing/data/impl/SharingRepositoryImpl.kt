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
        actionType = ActionType.SHARE_APP,
        content = context.getString(R.string.share_app_content)
    )

    override fun getContactSupport() = ShareAction(
        actionType = ActionType.CONTACT_SUPPORT,
        email = context.getString(R.string.contact_support_email),
        subject = buildString {
            append("${context.getString(R.string.contact_support_subject)} ")
            append(context.getString(R.string.app_name))
        },
        content = context.getString(R.string.contact_support_text)
    )

    override fun getOpenTerms() = ShareAction(
        actionType = ActionType.OPEN_TERMS,
        content = context.getString(R.string.user_agreement_content),
    )
}