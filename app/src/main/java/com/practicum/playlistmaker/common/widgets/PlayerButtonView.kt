package com.practicum.playlistmaker.common.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlayerButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val icon: Bitmap?
    private val activeIcon: Bitmap?
    private var bitmapImage: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private var isActive = false

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PlayerButtonView, defStyleAttr, defStyleRes).apply {
            try {
                icon = getDrawable(R.styleable.PlayerButtonView_icon)?.toBitmap()
                activeIcon = getDrawable(R.styleable.PlayerButtonView_activeIcon)?.toBitmap()
                bitmapImage = icon

                val tint = getColor(R.styleable.PlayerButtonView_tint, Color.TRANSPARENT)
                if (tint != Color.TRANSPARENT) {
                    paint.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                }
            } finally {
                recycle()
            }
        }
    }

    fun redraw(isActive: Boolean? = null) {
        setActive(isActive)
        bitmapImage = if (this.isActive ) activeIcon ?: icon else icon
        invalidate()
    }

    fun updateBtnState(isActive: Boolean, shouldPlayAnimation: Boolean = true) {
        if (!shouldPlayAnimation) {
            redraw(isActive)
            return
        }

        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)

        scaleDown.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                redraw(isActive)
                startAnimation(scaleUp)
            }
        })

        startAnimation(scaleDown)
    }

    private fun setActive(isActive: Boolean? = null) {
        this.isActive = isActive ?: !this.isActive
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        bitmapImage?.let {
            canvas.drawBitmap(it, null, imageRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                setActive()
                updateBtnState(isActive)
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}