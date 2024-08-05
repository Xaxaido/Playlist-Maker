package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.domain.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.resources.AppTheme
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.SettingsPresenter

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsPresenter by lazy {
        SettingsPresenter(
            ExternalNavigatorImpl(this),
            SettingsRepositoryImpl(this, getSharedPreferences(getString(R.string.prefs_file_name), MODE_PRIVATE)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.themeSwitch.isChecked = viewModel.getTheme() == AppTheme.SYSTEM.value
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitch.setOnClickListener {
            viewModel.toggleSystemTheme(binding.themeSwitch.isChecked)
        }

        binding.btnSettingsShare.setOnClickListener { viewModel.shareApp() }
        binding.btnContactSupport.setOnClickListener { viewModel.contactSupport() }
        binding.btnSettingsUserAgreement.setOnClickListener { viewModel.userAgreement() }
    }
}