package com.practicum.playlistmaker.common.widgets.recycler

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import com.practicum.playlistmaker.common.utils.Util
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class ParticleAnimator(
    private val context: Context,
    private val particleView: ParticleView,
    private val bitmap: Bitmap,
    private val startX: Float,
    private val startY: Float,
) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint()

    private var animator: ValueAnimator? = null

    init {
        generateParticles()
    }

    private fun calculateRowsAndColumns(screenDensity: Float, imageWidthPx: Int, imageHeightPx: Int): Pair<Int, Int> {
        val desiredParticleSizePx = (Util.PARTICLE_SIZE * screenDensity + 0.5f).toInt()
        val rows = ceil(imageHeightPx / desiredParticleSizePx.toDouble()).toInt()
        val cols = ceil(imageWidthPx / desiredParticleSizePx.toDouble()).toInt()

        return Pair(rows, cols)
    }

    private fun generateParticles() {
        val screenDensity = context.resources.displayMetrics.density
        val imageWidthPx = bitmap.width
        val imageHeightPx = bitmap.height
        val (rows, cols) = calculateRowsAndColumns(screenDensity, imageWidthPx, imageHeightPx)

        val cellWidth = imageWidthPx / cols
        val cellHeight = imageHeightPx / rows

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = startX + col * cellWidth
                val y = startY + row * cellHeight

                val srcX = col * cellWidth
                val srcY = row * cellHeight

                val angle = Math.toRadians(-45.0)
                val speed = (Math.random() * 10 + 5).toFloat()
                val vx = (speed * cos(angle)).toFloat()
                val vy = (speed * sin(angle)).toFloat()

                particles.add(
                    Particle(
                        x, y,
                        vx, vy,
                        srcX, srcY,
                        cellWidth, cellHeight,
                        255
                    )
                )
            }
        }
    }

    fun start(onAnimationEnd: () -> Unit) {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = Util.PARTICLE_DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                updateParticles(value)
                particleView.invalidate()
            }
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

    private fun updateParticles(progress: Float) {
        val initialProgress = 0f
        for (particle in particles) {
            if (progress > initialProgress) {
                val adjustedProgress = (progress - initialProgress) / (1 - initialProgress)
                particle.x += particle.vx * adjustedProgress
                particle.y += particle.vy * adjustedProgress

                val windEffect = Util.WIND_EFFECT * adjustedProgress
                particle.x += windEffect
                particle.y -= windEffect

                particle.alpha = (255 * (1 - adjustedProgress)).toInt()
            }
        }
    }

    fun drawParticles(canvas: Canvas) {
        for (particle in particles) {
            paint.alpha = particle.alpha
            canvas.drawBitmap(
                bitmap,
                Rect(particle.srcX, particle.srcY, particle.srcX + particle.width, particle.srcY + particle.height),
                particle.getBounds(),
                paint
            )
        }
    }
}