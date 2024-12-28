package com.practicum.playlistmaker.common.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.google.gson.Gson
import com.practicum.playlistmaker.common.resources.AppTheme
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.search.domain.model.Track
import kotlin.math.abs
import kotlin.math.hypot

object Util {

    const val COUNTRY_CSS_SELECTOR = "dd[data-testid=grouptext-section-content]"
    const val ANIMATION_SHORT= 250L
    const val UPDATE_PLAYBACK_PROGRESS_DELAY= 300L
    const val BUTTON_ENABLED_DELAY = 1000L
    const val USER_INPUT_DELAY = 2000L
    const val PARTICLE_DURATION = 2000L
    const val HISTORY_MAX_COUNT = 10
    const val NO_CONNECTION = -1
    const val HTTP_OK = 200
    const val HTTP_NOT_FOUND = 404
    const val INTERNAL_SERVER_ERROR = 500
    const val REQUEST_CANCELLED = -2
    const val ICON_SIZE = 32
    const val WIND_EFFECT = 1f
    const val PARTICLE_SIZE = 1.5f
    const val UNDERLAY_BUTTON_TEXT_SIZE = 30f
    const val UNDERLAY_BUTTON_WIDTH = 200

    fun jsonToTrack(json: String): Track = Gson().fromJson(json, Track::class.java)
    fun trackToJson(track: Track): String = Gson().toJson(track)

    fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                AppTheme.LIGHT.value -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK.value -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun getColor(context: Context, attr: Int): Int {
        val typedValue = TypedValue()

        return if (context.theme.resolveAttribute(attr, typedValue, true)) {
            typedValue.resourceId
        } else -1
    }

    fun drawableToBitmap(context: Context, image: Drawable?) =
        image?.toBitmap()?.scale(
            ICON_SIZE.dpToPx(context),
            ICON_SIZE.dpToPx(context),
            false
        )

    fun formatValue(value: Int, valueName: String): String {
        val valueNameToFormat = valueName.split(",")
        val preLastNumber = value % 100 / 10
        val lastNumber = value % 10

        if (preLastNumber == 1) {
            return "$value ${valueNameToFormat[2]}"
        }

        return when (lastNumber) {
            1 -> "$value ${valueNameToFormat[0]}"
            2, 3, 4 -> "$value ${valueNameToFormat[1]}"
            else -> "$value ${valueNameToFormat[2]}"
        }
    }

    fun drawFrame(view: View, density: Float, borderColor: Int) {
        val borderWidth = 1 * density
        val cornerRadius = 8 * density
        val dashLength = 32 * density
        val gapLength = 32 * density

        val currentBackground = view.background

        val dashedBorder = object : Drawable() {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = borderColor
                strokeWidth = borderWidth
                pathEffect = DashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
            }

            override fun draw(canvas: Canvas) {
                val rect = RectF(bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat(), bounds.bottom.toFloat())
                drawDashedRoundedRect(canvas, rect, cornerRadius, dashLength, gapLength, paint)
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                paint.colorFilter = colorFilter
            }

            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
        }

        val combinedBackground = LayerDrawable(arrayOf(currentBackground, dashedBorder))
        view.background = combinedBackground
    }

    fun drawDashedRoundedRect(
        canvas: Canvas,
        rect: RectF,
        cornerRadius: Float,
        dashLength: Float,
        gapLength: Float,
        paint: Paint,
    ) {
        val arcLength = Math.PI.toFloat() * cornerRadius / 2
        val straightLength = rect.width() - 2 * cornerRadius + rect.height() - 2 * cornerRadius
        val totalPerimeter = 2 * (straightLength + arcLength)

        val totalDashAndGapLength = dashLength + gapLength
        val dashCount = (totalPerimeter / totalDashAndGapLength).toInt()
        val adjustedGapLength = (totalPerimeter - dashCount * dashLength) / dashCount

        val topLeftCorner = RectF(rect.left, rect.top, rect.left + 2 * cornerRadius, rect.top + 2 * cornerRadius)
        val topRightCorner = RectF(rect.right - 2 * cornerRadius, rect.top, rect.right, rect.top + 2 * cornerRadius)
        val bottomRightCorner = RectF(rect.right - 2 * cornerRadius, rect.bottom - 2 * cornerRadius, rect.right, rect.bottom)
        val bottomLeftCorner = RectF(rect.left, rect.bottom - 2 * cornerRadius, rect.left + 2 * cornerRadius, rect.bottom)

        drawLine(canvas, paint, rect.left + cornerRadius, rect.top, rect.right - cornerRadius, rect.top, dashLength, adjustedGapLength)
        drawArc(canvas, paint, topRightCorner, 270f, dashLength, adjustedGapLength, cornerRadius)
        drawLine(canvas, paint, rect.right, rect.top + cornerRadius, rect.right, rect.bottom - cornerRadius, dashLength, adjustedGapLength)
        drawArc(canvas, paint, bottomRightCorner, 0f, dashLength, adjustedGapLength, cornerRadius)
        drawLine(canvas, paint, rect.right - cornerRadius, rect.bottom, rect.left + cornerRadius, rect.bottom, dashLength, adjustedGapLength)
        drawArc(canvas, paint, bottomLeftCorner, 90f, dashLength, adjustedGapLength, cornerRadius)
        drawLine(canvas, paint, rect.left, rect.bottom - cornerRadius, rect.left, rect.top + cornerRadius, dashLength, adjustedGapLength)
        drawArc(canvas, paint, topLeftCorner, 180f, dashLength, adjustedGapLength, cornerRadius)
    }

    private fun drawLine(canvas: Canvas, paint: Paint, startX: Float, startY: Float, endX: Float, endY: Float, dashLength: Float, adjustedGapLength: Float) {
        val segmentLength = hypot((endX - startX).toDouble(), (endY - startY).toDouble()).toFloat()
        var currentPos = 0f

        while (currentPos < segmentLength) {
            val dashEnd = (currentPos + dashLength).coerceAtMost(segmentLength)
            val fraction = dashEnd / segmentLength

            val dashStartX = startX + currentPos / segmentLength * (endX - startX)
            val dashStartY = startY + currentPos / segmentLength * (endY - startY)
            val dashEndX = startX + fraction * (endX - startX)
            val dashEndY = startY + fraction * (endY - startY)

            canvas.drawLine(dashStartX, dashStartY, dashEndX, dashEndY, paint)
            currentPos += dashLength + adjustedGapLength
        }
    }

    private fun drawArc(canvas: Canvas, paint: Paint, rect: RectF, startAngle: Float, dashLength: Float, adjustedGapLength: Float, cornerRadius: Float) {
        val path = android.graphics.Path()
        val arcLength = Math.PI.toFloat() * cornerRadius * abs(90f) / 180
        var currentPos = 0f

        while (currentPos < arcLength) {
            val dashEnd = (currentPos + dashLength).coerceAtMost(arcLength)
            val startFraction = currentPos / arcLength
            val endFraction = dashEnd / arcLength

            path.reset()
            path.arcTo(rect, startAngle + startFraction * 90f, (endFraction - startFraction) * 90f, false)
            canvas.drawPath(path, paint)
            currentPos += dashLength + adjustedGapLength
        }
    }
}