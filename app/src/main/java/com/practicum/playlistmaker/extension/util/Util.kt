package com.practicum.playlistmaker.extension.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.Group
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.view.WindowCompat
import com.practicum.playlistmaker.extension.recycler.ICON_SIZE
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale

object Util {

    var View.isVisible: Boolean
        get() = visibility == View.VISIBLE
        set(value) { visibility = if (value) View.VISIBLE else View.INVISIBLE }

    fun String.toDate() = SimpleDateFormat("yyyy", Locale.getDefault())
        .parse(this)
        ?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.year
        ?.toString()
        ?: ""

    fun Long.millisToSeconds() = SimpleDateFormat(
        "mm:ss",
        Locale.getDefault()
    ).format(this) ?: ""

    fun Int.dpToPx(context: Context) = (this * context.resources.displayMetrics.density).toInt()

    fun toggleDarkTheme(isEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun drawableToBitmap(context: Context, image: Drawable?) =
        image?.toBitmap()?.scale(
            ICON_SIZE.dpToPx(context),
            ICON_SIZE.dpToPx(context),
            false
        )

    @Suppress("DEPRECATION")
    fun softInputAdjustResize(window: Window, view: Group) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            view.setOnApplyWindowInsetsListener { _, insets ->
                val topInset = insets.getInsets(WindowInsets.Type.statusBars()).top
                val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom
                val navigationHeight = insets.getInsets(WindowInsets.Type.navigationBars()).bottom
                val bottomInset = if (imeHeight == 0) navigationHeight else imeHeight
                view.setPadding(0, topInset, 0, bottomInset)
                insets
            }
        } else {
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            )
        }
    }
}