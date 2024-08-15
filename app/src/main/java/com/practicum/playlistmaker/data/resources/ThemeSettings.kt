package com.practicum.playlistmaker.data.resources

import android.content.Context
import android.content.res.Configuration

class ThemeSettings(
    private val context: Context,
    isSystemThemeEnabled: Boolean,
) {

    val appTheme: String

    init { appTheme = getThemeName(isSystemThemeEnabled) }

    private fun getThemeName(isSystemThemeEnabled: Boolean) =
        if (isSystemThemeEnabled) {
            AppTheme.SYSTEM.value
        } else {
            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppTheme.DARK.value
                }
                else -> {
                    AppTheme.LIGHT.value
                }
            }
        }
}