package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.data.model.ActionType
import com.practicum.playlistmaker.sharing.data.model.ShareAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        viewModel.settingsLiveData.observe(this, ::setTheme)
        viewModel.sharingLiveData.observe(this, ::prepareIntent)
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

    private fun prepareIntent(action: ShareAction) {
        when (action.actionType) {
            ActionType.SHARE_APP -> {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, action.extra[ShareAction.CONTENT])
                    startActivity(Intent.createChooser(this, null))
                }
            }
            ActionType.CONTACT_SUPPORT -> {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(action.extra[ShareAction.EMAIL]))
                    putExtra(Intent.EXTRA_SUBJECT, action.extra[ShareAction.SUBJECT])
                    putExtra(Intent.EXTRA_TEXT, action.extra[ShareAction.CONTENT])
                    startActivity(this)
                }
            }
            ActionType.OPEN_TERMS -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(action.extra[ShareAction.CONTENT])
                    startActivity(this)
                }
            }
        }
    }
}