package com.practicum.playlistmaker.common.widgets.recycler

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.Extensions.dpToPx
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.common.utils.Util.ICON_SIZE
import com.practicum.playlistmaker.common.utils.Util.drawableToBitmap

class UnderlayButton(
    private val context: Context,
    private var text: String,
    @DrawableRes iconId: Int,
    private val bgColor: Int,
    private val textColor: Int,
    private val onClick: (Int) -> Unit = {},
) {

    private var pos = 0
    private var clickRegion: RectF? = null
    private val image: Bitmap?

    init {
        AppCompatResources.getDrawable(context, iconId).apply {
            image = drawableToBitmap(context, this)
        }
    }

    fun handleClick(event: MotionEvent) {
        clickRegion?.let {
            if (it.contains(event.x, event.y)) {
                onClick(pos)
            }
        }
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {

        val r = Rect()
        val p = Paint()
        val cHeight = rectF.height()

        p.color = bgColor
        c.drawRect(rectF, p)

        image?.let { img ->
            val iconHorizontalMargin = Util.UNDERLAY_BUTTON_WIDTH / 2 - ICON_SIZE.dpToPx(context) / 2
            val iconVerticalMargin = cHeight / 2 - img.height / 2 - Util.UNDERLAY_BUTTON_TEXT_SIZE  / 1.5f

            c.drawBitmap(
                img,
                rectF.left + iconHorizontalMargin,
                rectF.top + iconVerticalMargin,
                p
            )
        }

        p.color = textColor
        p.typeface = ResourcesCompat.getFont(context, R.font.ys_500)
        p.textSize = Util.UNDERLAY_BUTTON_TEXT_SIZE 
        p.isAntiAlias = true
        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text, 0, text.length, r)

        val x = Util.UNDERLAY_BUTTON_WIDTH / 2 - r.width() / 2
        val y = cHeight / 2 + r.height() / 2 - r.top + Util.UNDERLAY_BUTTON_TEXT_SIZE 

        c.drawText(text, rectF.left + x, rectF.top + y - 10, p)

        clickRegion = rectF
        this.pos = pos
    }
}