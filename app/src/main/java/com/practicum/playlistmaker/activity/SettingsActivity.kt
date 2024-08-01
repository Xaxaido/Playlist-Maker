package com.practicum.playlistmaker.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.resources.AppTheme
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val prefs: App by lazy { applicationContext as App }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        binding.themeSwitch.isChecked = prefs.appTheme == AppTheme.SYSTEM.value
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.themeSwitch.setOnClickListener { setTheme() }

        binding.btnSettingsShare.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_content))
                startActivity(this, null)
            }
        }

        binding.btnContactSupport.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.contact_support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_support_text))
            }

            if (packageManager.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            }
        }

        binding.btnSettingsUserAgreement.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.user_agreement_content))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (packageManager.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            }
        }
    }

    private fun setTheme() {
        updateTheme(
            if (binding.themeSwitch.isChecked) {
                AppTheme.SYSTEM.value
            } else {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        AppTheme.DARK.value
                    }
                    else -> {
                        AppTheme.LIGHT.value
                    }
                }
            }
        )
    }

    private fun updateTheme(theme: String) {
        prefs.saveTheme(theme)
        prefs.applyTheme(theme)
    }
}