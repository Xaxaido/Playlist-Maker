package com.practicum.playlistmaker.settings.data.impl

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.DtoConverter.toThemeSettings
import com.practicum.playlistmaker.settings.data.dto.ThemeSettingsDto
import com.practicum.playlistmaker.common.resources.AppTheme
import com.practicum.playlistmaker.settings.data.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val context: Context,
    private val prefs: SharedPreferences,
) : SettingsRepository {

    private val key = context.getString(R.string.theme_auto)

    override fun getThemeSettings(): ThemeSettings {
        val themeSettingsDto = ThemeSettingsDto(context, prefs.getBoolean(key, true))
        return themeSettingsDto.toThemeSettings()
    }

    override fun getThemeSwitchState() = getThemeSettings().themeName == AppTheme.SYSTEM.value

    override fun updateThemeSetting(isChecked: Boolean) {
        prefs.edit()
            .putBoolean(key, isChecked)
            .apply()
    }
}