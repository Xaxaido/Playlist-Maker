package com.practicum.playlistmaker.common.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MySnackBar(
    private val view: View,
    private val message: String,
) {

    fun show() {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            val color = Util.getColor(context, android.R.attr.colorBackground)
            view.setBackgroundColor(ContextCompat.getColor(context, color))
            show()
        }
    }
}