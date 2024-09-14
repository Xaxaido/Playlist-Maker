package com.practicum.playlistmaker.common.widgets.recycler

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var animator: ParticleAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        animator?.drawParticles(canvas)
    }

    fun startAnimation(onAnimationEnd: () -> Unit) {
        animator?.start(onAnimationEnd)
    }
}