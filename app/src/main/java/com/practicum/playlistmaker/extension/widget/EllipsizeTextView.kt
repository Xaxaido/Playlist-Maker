package com.practicum.playlistmaker.extension.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

private const val DRAWABLE_SIZE = 3
private const val SIDE_MARGIN = 5
private const val ELLIPSIZE_SYMBOL = "..."

class EllipsizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private fun String.width() = paint.measureText(this)
    private fun Int.dpToPx() = (this * context.resources.displayMetrics.density).toInt()

    private lateinit var sourceText: String
    private lateinit var extraText: String
    private var drawableSize = DRAWABLE_SIZE.dpToPx()
    private val sideMargin = SIDE_MARGIN.dpToPx()
    private val availableWidth: Float by lazy {
        measuredWidth - (ELLIPSIZE_SYMBOL.width() + drawableSize + sideMargin * 2) - extraText.width()
    }
    private val drawable = ShapeDrawable(OvalShape()).apply {
        paint.color = currentTextColor
        setBounds(0, 0, drawableSize, drawableSize)
    }

    fun setText(sourceText: String, extraText: String) {
        this.sourceText = sourceText
        this.extraText = extraText
        requestLayout()
    }

    private fun ellipsize(sourceText: String): String {
        var result = sourceText

        while (result.width() > availableWidth) {
            result = result.dropLast(1)
        }

        return result + ELLIPSIZE_SYMBOL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (measuredWidth > 0) {
            val isFullVisible = sourceText.width() <= availableWidth
            text = updateText(if (isFullVisible) sourceText else ellipsize(sourceText))
        }
    }

    private fun buildSpan(builder: SpannableStringBuilder, span: Any) {
        builder.append("\u200B")
        builder.setSpan(
            span,
            builder.length - 1,
            builder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun updateText(ellipsizedText: String) = SpannableStringBuilder(ellipsizedText).apply {
        buildSpan(this, MarginSpan(sideMargin))
        buildSpan(this, ImageSpan(drawable, DynamicDrawableSpan.ALIGN_CENTER))
        buildSpan(this, MarginSpan(sideMargin))
        append(extraText)
    }

    private class MarginSpan(
        private val margin: Int
    ) : ReplacementSpan() {

        override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) = margin
        override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {}
    }
}