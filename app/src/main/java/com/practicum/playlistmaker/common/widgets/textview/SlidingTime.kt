package com.practicum.playlistmaker.common.widgets.textview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import com.practicum.playlistmaker.R

class SlidingTime @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val oldTimeDigits: List<TextView>
    private val newTimeDigits: List<TextView>
    private val baseText: String
    private var textColor = 0
    private var textSize = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sliding_time, this, true)

        oldTimeDigits = listOf(
            findViewById(R.id.oldMinuteTens),
            findViewById(R.id.oldMinuteOnes),
            findViewById(R.id.oldSecondTens),
            findViewById(R.id.oldSecondOnes),
        )

        newTimeDigits = listOf(
            findViewById(R.id.newMinuteTens),
            findViewById(R.id.newMinuteOnes),
            findViewById(R.id.newSecondTens),
            findViewById(R.id.newSecondOnes),
        )

        attrs?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(it,
                R.styleable.SlidingTime, 0, 0)
            try {
                textColor = typedArray.getColor(R.styleable.SlidingTime_textColor, Color.BLACK)
                textSize = typedArray.getDimensionPixelSize(R.styleable.SlidingTime_textSize, 14)
            } finally {
                typedArray.recycle()
            }
        }

        baseText = context.getString(R.string.default_duration_start)
        applyTextAppearance()
    }

    private fun applyTextAppearance() {
        val allDigits = oldTimeDigits + newTimeDigits + listOf(
            findViewById(R.id.delimiter_old),
            findViewById(R.id.delimiter_new),
        )

        for (digit in allDigits) {
            digit.gravity = Gravity.CENTER
            digit.setTextColor(textColor)
            digit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }

        allDigits[0].post {
            val viewWidth = allDigits[0].measuredWidth
            for (digit in allDigits) {
                digit.width = viewWidth
            }
        }
    }

    fun setTime(newTime: String) {
        val oldTime = getCurrentTime()
        val newDigits = newTime.filter { it.isDigit() }

        for (i in newDigits.indices) {
            val oldChar = oldTime[i]
            val newChar = newDigits[i]

            if (oldChar != newChar) {
                animateDigitChange(i, newChar)
            }
        }
    }

    fun reset() {
        val defaultTime = baseText.filter { it.isDigit() }

        for (i in defaultTime.indices) {
            animateDigitChange(i, defaultTime[i])
        }
    }

    private fun getCurrentTime(): String {
        return oldTimeDigits.joinToString("") { it.text.toString() }
    }

    private fun animateDigitChange(index: Int, newChar: Char) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.show_time)
        val slideOut = AnimationUtils.loadAnimation(context, R.anim.hide_time)

        oldTimeDigits[index].startAnimation(slideOut)
        newTimeDigits[index].text = newChar.toString()
        newTimeDigits[index].startAnimation(slideIn.apply {
            setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    oldTimeDigits[index].text = newChar.toString()
                }
            })
        })
    }
}