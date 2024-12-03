package com.practicum.playlistmaker.common.utils

import android.app.Activity

import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R

class MySnackBar(
    private val activity: Activity,
    private val message: String,
) {

    fun show() {
        Snackbar.make(
            activity.findViewById(R.id.content_layout),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            val color = Util.getColor(activity, android.R.attr.colorBackground)
            view.setBackgroundColor(ContextCompat.getColor(activity, color))
            show()
        }
    }
}