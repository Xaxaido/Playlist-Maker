package com.practicum.playlistmaker.common.widgets.recycler

import android.graphics.RectF

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var srcX: Int,
    var srcY: Int,
    var width: Int,
    var height: Int,
    var alpha: Int = 255
) {
    fun getBounds(): RectF {
        return RectF(x, y, x + width, y + height)
    }
}