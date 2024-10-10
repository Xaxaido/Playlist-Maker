package com.practicum.playlistmaker.common.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Blur

class BlurredImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100L
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            updateBlur()
        }
    }
    private lateinit var contentView: View

    private fun updateBlur() {
        captureContent().let {
            if (it != null) Blur.blur(context, this, it)
        }
    }

    private fun captureContent(): Bitmap? {
        val contentTop = top - contentView.top
        val contentHeight = contentView.height
        val startY = when(contentTop + measuredHeight) {
            in 0..contentHeight -> contentTop
            in contentHeight + 1..contentHeight + measuredHeight -> contentTop - measuredHeight
            else -> -1
        }

        if (startY == -1 || contentHeight == 0) return null

        val bitmap = Bitmap.createBitmap(width, contentHeight, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            translate(0f, (-contentView.scrollY).toFloat())
            if (contentView.isVisible) contentView.draw(this)
            else drawColor(Color.TRANSPARENT)
        }

        return Bitmap.createBitmap(bitmap, 0, startY, width, measuredHeight)
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        contentView = rootView.findViewById(R.id.recycler)
        post { animator.start() }
    }
}