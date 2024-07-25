package com.practicum.playlistmaker.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val prefs = applicationContext as App

        binding.switchDarkMode.isChecked = prefs.darkTheme
        binding.switchDarkMode.setOnClickListener { view ->
            val isChecked = (view as SwitchCompat).isChecked

            prefs.saveDarkThemeState(isChecked)
            prefs.toggleDarkTheme(isChecked)
        }

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
}