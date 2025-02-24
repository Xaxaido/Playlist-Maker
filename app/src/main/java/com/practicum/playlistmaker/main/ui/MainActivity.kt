package com.practicum.playlistmaker.main.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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
import com.practicum.playlistmaker.main.ui.view_model.MainActivityViewModel
import com.practicum.playlistmaker.medialibrary.ui.CreatePlaylistFragment
import com.practicum.playlistmaker.medialibrary.ui.PlaylistFragment
import com.practicum.playlistmaker.player.ui.PlayerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), BackButtonState {

    private val viewModel by viewModel<MainActivityViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override var customNavigateAction: (() -> Boolean)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setListeners()
    }

    private fun setupUI() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

            when (destination.id) {
                R.id.player_fragment -> {
                    if (currentFragment !is CreatePlaylistFragment
                        && currentFragment !is PlaylistFragment) updateBottomNav(false)
                }
                R.id.create_playlist_fragment, R.id.playlist_fragment -> {
                    if (currentFragment !is PlayerFragment) updateBottomNav(false)
                }
                else -> {
                    if (!binding.bottomNavContainer.isVisible) {
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
            if (isVisible) R.anim.slide_in_left else R.anim.slide_out_left,
        ).apply {
            setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    binding.bottomNavContainer.isVisible = isVisible
                }
            })
            binding.bottomNavContainer.startAnimation(this)
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
        if (!binding.noInternetMsg.isVisible) return

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

    override fun setCustomNavigation(action: () -> Boolean) {
        customNavigateAction = action
    }

    override fun setIconColor(isDefault: Boolean) {
        val color = if (isDefault) {
            TypedValue().apply {
                theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, this, true)
            }.data
        } else {
            ContextCompat.getColor(this, R.color.black)
        }

        supportActionBar?.apply {
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_back)?.apply {
                    setTint(color)
                })
        }
    }

    override fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        val result: Boolean

        if (customNavigateAction != null) {
            result = customNavigateAction!!.invoke()
        } else {
            result = navController.navigateUp() || super.onSupportNavigateUp()
        }

        return result
    }
}