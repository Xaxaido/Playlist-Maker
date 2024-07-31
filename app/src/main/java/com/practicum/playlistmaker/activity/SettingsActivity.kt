package com.practicum.playlistmaker.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.model.resources.AppTheme
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.extension.util.Debounce

private const val ANIMATION_DURATION = 500L

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val prefs: App by lazy { applicationContext as App }
    private var initialHeight = 0
    private val listener = RadioGroup.OnCheckedChangeListener  { _, checkedId ->
        if (binding.switchThemeAuto.isChecked) binding.switchThemeAuto.isChecked = false
        handleTheme(
            when (checkedId) {
                R.id.btn_radio_light -> AppTheme.LIGHT.value
                else -> AppTheme.DARK.value
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (prefs.appTheme) {
            AppTheme.LIGHT.value -> binding.btnRadioLight.isChecked = true
            AppTheme.DARK.value -> binding.btnRadioDark.isChecked = true
        }

        setListeners()
        binding.switchThemeAuto.isChecked = prefs.appTheme == AppTheme.SYSTEM.value
        if (binding.switchThemeAuto.isChecked) {
            binding.lightDarkTheme.post {
                val layoutParams = binding.lightDarkTheme.layoutParams

                layoutParams.height = binding.themeAuto.measuredHeight
                binding.lightDarkTheme.layoutParams = layoutParams
                updateConstraints(true)

                for (id in binding.themeGroup.referencedIds) {
                    findViewById<View>(id).isVisible = false
                }
            }
        } else binding.themeGroup.isVisible = true
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.themeRadioGroup.setOnCheckedChangeListener(listener)

        binding.lightDarkTheme.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                binding.lightDarkTheme.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initialHeight = binding.lightDarkTheme.height
            }
        })

        binding.switchThemeAuto.setOnClickListener { _ ->
            handleTheme(
                if (binding.switchThemeAuto.isChecked) {
                    animateView(binding.lightDarkTheme, true)
                    binding.themeRadioGroup.apply {
                        setOnCheckedChangeListener(null)
                        clearCheck()
                        setOnCheckedChangeListener(listener)
                    }
                    AppTheme.SYSTEM.value
                } else {
                    animateView(binding.lightDarkTheme, false)
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            binding.btnRadioLight.isChecked = true
                            AppTheme.LIGHT.value
                        }
                        else -> {
                            binding.btnRadioDark.isChecked = true
                            AppTheme.DARK.value
                        }
                    }
                }, ANIMATION_DURATION
            )
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

    private fun handleTheme(theme: String, delay: Long = 0L) {
        prefs.saveTheme(theme)
        Debounce(delay = delay) { prefs.applyTheme(theme) }.start()
    }

    private fun animateView(view: View, hide: Boolean, duration: Long = ANIMATION_DURATION) {
        view.post {
            val finalHeight = if (hide) binding.themeAuto.measuredHeight else initialHeight
            val heightAnimator = ValueAnimator.ofInt(view.height, finalHeight).apply {
                val layoutParams = view.layoutParams

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    layoutParams.height = value
                    view.layoutParams = layoutParams
                }
                addListener(onEnd = { updateConstraints(hide) })
            }

            val animations = mutableListOf<ObjectAnimator>()
            binding.themeGroup.isVisible = !hide
            for (id in binding.themeGroup.referencedIds) {
                val child = findViewById<View>(id)
                val alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", if (hide) 1.0f else 0.0f, if (hide) 0.0f else 1.0f)

                child.isVisible = true
                animations.add(alphaAnimator)
            }

            val yAnimator = ObjectAnimator.ofFloat(binding.themeAuto, "y", if (hide) 0f else initialHeight - binding.themeAuto.measuredHeight.toFloat())

            AnimatorSet().apply {
                playTogether(heightAnimator, *animations.toTypedArray(), yAnimator)
                this.duration = duration
                start()
            }
        }
    }

    private fun updateConstraints(hide: Boolean) {
        val viewId = binding.themeAuto.id
        val constraintSet = ConstraintSet().apply {
            clone(binding.contentLayout)
            clear(viewId, ConstraintSet.TOP)
        }
        if (hide) {
            constraintSet.connect(viewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        } else {
            constraintSet.connect(viewId, ConstraintSet.TOP, binding.themeRadioGroup.id, ConstraintSet.BOTTOM, 0)
        }
        constraintSet.applyTo(binding.contentLayout)
    }
}