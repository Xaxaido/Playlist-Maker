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
import android.util.TypedValue
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
    var gradientWidth = 0
    private var paint: Paint
    private var textWidth = 0f
    private var animationOffset = 0f
    private val gap = 100f
    private var gradient: LinearGradient? = null
    private val scrollInterpolator = LinearInterpolator()

    init {
        isSingleLine = true
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = this@AutoScrollTextView.textSize
            color = this@AutoScrollTextView.currentTextColor
        }
    }

    fun setParams(size: Float) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                size,
                context.resources.displayMetrics
            )
        }
    }

    fun setString(string: String) {
        text = string
        textWidth = paint.measureText(string)
        scrollDuration = ((textWidth + gap) / 100 * 1000).toLong()
        requestLayout()
    }

    fun startScrolling() {
        if (textWidth + gradientWidth < width || width == 0) return

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val textHeight = paint.fontMetrics.run { bottom - top }
        val desiredHeight = (paddingTop + textHeight + paddingBottom).toInt()

        val resolvedHeight = resolveSize(desiredHeight, heightMeasureSpec)
        val resolvedWidth = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(resolvedWidth, resolvedHeight)
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