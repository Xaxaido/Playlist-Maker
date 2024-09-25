package com.practicum.playlistmaker.main.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.practicum.playlistmaker.common.resources.InternetState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.main.ui.view_model.MainActivityModel
import com.practicum.playlistmaker.medialibrary.ui.MediaLibraryActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private  fun setListeners() {
        viewModel.liveData.observe(this, ::renderState)
        binding.btnCloseNoInternetMsg.setOnClickListener { hideErrorMsg() }

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnMediaLibrary.setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun updateErrorMessage(from: Float, to: Float, onAnimationEnd: () -> Unit = {}) {
        ObjectAnimator.ofFloat(binding.noInternetMsg, "translationY", from, to).apply {
            duration = Util.ANIMATION_SHORT
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

    private fun hideErrorMsg() {
        if (!binding.noInternetMsg.isVisible ) return

        updateErrorMessage(0f, -binding.noInternetMsg.height.toFloat()) {
            binding.noInternetMsg.visibility = View.INVISIBLE
        }
    }

    private fun showErrorMsg() {
        binding.noInternetMsg.isVisible = true
        updateErrorMessage(-binding.noInternetMsg.height.toFloat(), 0f)
    }

    private fun renderState(state: InternetState) {
        when (state) {
            is InternetState.Connected -> hideErrorMsg()
            is InternetState.ConnectionLost -> showErrorMsg()
        }
    }
}