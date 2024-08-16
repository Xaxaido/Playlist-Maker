package com.practicum.playlistmaker.presentation.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val presenter: SettingsPresenter by lazy { Creator.getSettingsPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.themeSwitch.isChecked = presenter.getThemeSwitchState()
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitch.setOnClickListener {
            presenter.toggleSystemTheme(binding.themeSwitch.isChecked)
        }

        binding.btnSettingsShare.setOnClickListener { presenter.shareApp() }
        binding.btnContactSupport.setOnClickListener { presenter.contactSupport() }
        binding.btnSettingsUserAgreement.setOnClickListener { presenter.userAgreement() }
    }
}