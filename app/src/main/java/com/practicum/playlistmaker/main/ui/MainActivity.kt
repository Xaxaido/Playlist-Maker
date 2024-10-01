package com.practicum.playlistmaker.main.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.resources.InternetState
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.main.domain.api.BackButtonState
import com.practicum.playlistmaker.main.ui.view_model.MainActivityModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BackButtonState {

    private val viewModel: MainActivityModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setListeners()
    }

    private fun setupUI() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.player_fragment -> {
                    updateBottomNav(false)
                }
                else -> {
                    if (!binding.bottomNav.isVisible ) {
                        updateBottomNav(true)
                    }
                }
            }
        }

        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.search_fragment, R.id.media_library_fragment, R.id.settings_fragment, R.id.player_fragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setListeners() {
        viewModel.liveData.observe(this, ::renderState)
        binding.btnCloseNoInternetMsg.setOnClickListener { hideErrorMsg() }
    }

    private fun updateBottomNav(isVisible: Boolean) {
        AnimationUtils.loadAnimation(
            this,
            if (isVisible) R.anim.fade_in else R.anim.fade_out
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    binding.bottomNav.isVisible = isVisible
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            binding.bottomNav.startAnimation(this)
        }
    }

    private fun updateErrorMsg(target: Float, onAnimationEnd: () -> Unit = {}) {
        ObjectAnimator.ofFloat(binding.noInternetMsg, "translationY", target).apply {
            duration = Util.ANIMATION_SHORT
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

    private fun hideErrorMsg() {
        if (!binding.noInternetMsg.isVisible ) return

        updateErrorMsg(-binding.noInternetMsg.height.toFloat()) {
            binding.noInternetMsg.visibility = View.INVISIBLE
        }
    }

    private fun showErrorMsg() {
        binding.noInternetMsg.isVisible = true
        updateErrorMsg(0f)
    }

    private fun renderState(state: InternetState) {
        when (state) {
            is InternetState.Connected -> hideErrorMsg()
            is InternetState.ConnectionLost -> showErrorMsg()
        }
    }

    override fun updateBackBtn(enabled: Boolean) {
        supportActionBar?.apply { setDisplayHomeAsUpEnabled(enabled) }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}