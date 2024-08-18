package com.practicum.playlistmaker.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SharingRepository

class SharingRepositoryImpl(
    private val context: Context,
) : SharingRepository {

    override fun shareApp() {
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app_content))
        }.let { intent ->
            if (context.packageManager.resolveActivity(intent, 0) != null) {
                val share = Intent.createChooser(intent, null).apply {
                    flags  = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(share)
            }
        }
    }

    override fun contactSupport() {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.contact_support_email)))
            putExtra(
                Intent.EXTRA_SUBJECT,
                buildString {
                    append("${context.getString(R.string.contact_support_subject)} ")
                    append(context.getString(R.string.app_name))
                }
            )
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.contact_support_text))
        }.let { intent ->
            if (context.packageManager.resolveActivity(intent, 0) != null)
                context.startActivity(intent)
        }
    }

    override fun userAgreement() {
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(context.getString(R.string.user_agreement_content))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.let { intent ->
            if (context.packageManager.resolveActivity(intent, 0) != null)
                context.startActivity(intent)
        }
    }
}