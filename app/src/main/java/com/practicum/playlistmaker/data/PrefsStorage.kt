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

    fun getString(key: String, defaultValue: String = "") = prefs.getString(key, defaultValue) ?: ""

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}