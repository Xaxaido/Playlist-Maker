package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        viewModel.liveData.observe(this) { isChecked ->
            binding.themeSwitch.isChecked = isChecked
            viewModel.saveTheme(isChecked)
            Util.applyTheme(viewModel.getCurrentTheme())
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitch.setOnClickListener {
            viewModel.toggleSystemTheme(binding.themeSwitch.isChecked)
        }

        binding.btnSettingsShare.setOnClickListener { viewModel.shareApp() }
        binding.btnContactSupport.setOnClickListener { viewModel.contactSupport() }
        binding.btnSettingsUserAgreement.setOnClickListener { viewModel.openTerms() }
    }
}