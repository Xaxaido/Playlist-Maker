package com.practicum.playlistmaker.data

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.R

class PrefsStorage(
    context: Context,
) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.prefs_file_name), MODE_PRIVATE)
    }

    fun getBoolean(key: String) = prefs.getBoolean(key, false)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String) = prefs.getString(key, "") ?: ""

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}