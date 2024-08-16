package com.practicum.playlistmaker.presentation.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.util.Blur

class BlurredImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100L
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            updateBlur()
        }
    }
    @IdRes
    private var parentViewId: Int = NO_ID
    private lateinit var targetView: View
    private lateinit var contentView: View
    private var targetHeight = 0

    init {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs,
            R.styleable.BlurredImageView, 0, 0)
        try {
            parentViewId = a.getResourceId(R.styleable.BlurredImageView_parentView, NO_ID)
        } finally {
            a.recycle()
        }
    }

    private fun setSize() {
        targetView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                targetHeight = targetView.height
                if (targetHeight == 0) return

                targetView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val lp = layoutParams
                lp.height = height
                layoutParams = lp
                post { animator.start() }
            }
        })
    }

    private fun updateBlur() {
        captureContent().let {
            if (it != null) Blur.blur(context, this, it)
        }
    }

    private fun captureContent(): Bitmap? {
        val contentHeight = contentView.height
        val startY = when(top + targetHeight) {
            in 0..contentHeight -> top
            in contentHeight + 1..contentHeight + targetHeight -> top - targetHeight
            else -> -1
        }

        if (startY == -1) return null

        val bitmap = Bitmap.createBitmap(width, contentHeight, Bitmap.Config.ARGB_8888)

        Canvas(bitmap).apply {
            translate(0f, (-contentView.scrollY).toFloat())
            contentView.draw(this)
        }

        return Bitmap.createBitmap(bitmap, 0, startY, width, targetHeight)
    }

    override fun onDetachedFromWindow() {
        animator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        targetView = rootView.findViewById(parentViewId)
        contentView = rootView.findViewById(R.id.recycler)
        setSize()
    }
}