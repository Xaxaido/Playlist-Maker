package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.widgets.BaseFragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.sharing.domain.model.ActionType
import com.practicum.playlistmaker.sharing.domain.model.IntentAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        viewModel.settingsLiveData.observe(viewLifecycleOwner, ::setTheme)
        viewModel.sharingLiveData.observe(viewLifecycleOwner, ::startIntent)

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