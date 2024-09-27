package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.model.ActionType
import com.practicum.playlistmaker.sharing.domain.model.IntentAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        viewModel.settingsLiveData.observe(this, ::setTheme)
        viewModel.sharingLiveData.observe(this, ::startIntent)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitch.setOnClickListener {
            viewModel.toggleSystemTheme(binding.themeSwitch.isChecked)
        }

        binding.btnSettingsShare.setOnClickListener { viewModel.shareApp() }
        binding.btnContactSupport.setOnClickListener { viewModel.contactSupport() }
        binding.btnSettingsUserAgreement.setOnClickListener { viewModel.openTerms() }
    }

    private fun setTheme(isChecked: Boolean) {
        binding.themeSwitch.isChecked = isChecked
        viewModel.saveTheme(isChecked)
        Util.applyTheme(viewModel.getCurrentTheme())
    }

    private fun startIntent(action: IntentAction) {
        when (action.actionType) {
            ActionType.SHARE_APP -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, action.extra[IntentAction.CONTENT])
                    startActivity(Intent.createChooser(this, null))
                }
            }
            ActionType.CONTACT_SUPPORT -> {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(action.extra[IntentAction.EMAIL]))
                    putExtra(Intent.EXTRA_SUBJECT, action.extra[IntentAction.SUBJECT])
                    putExtra(Intent.EXTRA_TEXT, action.extra[IntentAction.CONTENT])
                    startActivity(this)
                }
            }
            ActionType.OPEN_TERMS -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(action.extra[IntentAction.CONTENT])
                    startActivity(this)
                }
            }
        }
    }
}