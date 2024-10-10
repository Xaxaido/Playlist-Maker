package com.practicum.playlistmaker.common.widgets.textview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
import com.practicum.playlistmaker.R

class AutoScrollTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var scrollDelay = 2000L
    var scrollDuration = 0L
    private val paint: Paint
    private var textWidth = 0f
    private var animationOffset = 0f
    private val gap = 100f
    private var gradient: LinearGradient? = null
    private var gradientWidth = 0
    private val scrollInterpolator = LinearInterpolator()

    init {
        isSingleLine = true
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = this@AutoScrollTextView.textSize
            color = this@AutoScrollTextView.currentTextColor
        }

        attrs?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(it,
                R.styleable.AutoScrollTextView, 0, 0)
            try {
                gradientWidth = typedArray.getDimensionPixelSize(R.styleable.AutoScrollTextView_gradientWidth, 0)
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setString(string: String) {
        text = string
        textWidth = paint.measureText(string)
        scrollDuration = ((textWidth + gap) / 100 * 1000).toLong()
        requestLayout()
    }

    fun startScrolling() {
        if (textWidth + gradientWidth < width) return

        val scrollDistance = textWidth + gap
        ValueAnimator.ofFloat(0f, -scrollDistance).apply {
            duration = scrollDuration
            interpolator = scrollInterpolator
            startDelay = 2000L
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    postDelayed({
                        startDelay = 0L
                        start()
                    }, scrollDelay)
                }
            })
            addUpdateListener { animation ->
                animationOffset = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val gradientRatio = gradientWidth / (w / 100) * .01f
        gradient = LinearGradient(
            0f, 0f, w.toFloat(), 0f,
            intArrayOf(Color.TRANSPARENT, paint.color, paint.color, Color.TRANSPARENT),
            floatArrayOf(0f, gradientRatio, 1f - gradientRatio, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        if (text.isEmpty()) return

        val containerWidth = width.toFloat()
        val startX = animationOffset + gradientWidth
        val textBaseline = height / 2f - (paint.descent() + paint.ascent()) / 2

        canvas.drawText(text, startX, textBaseline, paint)

        if (textWidth > containerWidth - gradientWidth) {
            val endX = startX + textWidth + gap

            if (endX < containerWidth + textWidth) {
                canvas.drawText(text, startX + textWidth + gap, textBaseline, paint)
            }
        }
    }
}