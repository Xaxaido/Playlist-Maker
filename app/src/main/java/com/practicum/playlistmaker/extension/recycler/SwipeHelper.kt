package com.practicum.playlistmaker.extension.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.source.TrackAdapter
import com.practicum.playlistmaker.extension.util.Util.dpToPx
import com.practicum.playlistmaker.extension.util.Util.drawableToBitmap
import java.util.LinkedList
import java.util.Queue

private const val BUTTON_WIDTH = 200
const val ICON_SIZE = 32
private const val TEXT_SIZE = 30f

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val touchListener: TouchedTrackListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    lateinit var buttons: MutableList<UnderlayButton>
    private lateinit var gestureDetector: GestureDetector
    private var swipedPos = -1
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>>
    private lateinit var recoverQueue: Queue<Int>

    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (button in buttons) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }

    private val onTouchListener = OnTouchListener { _, e ->
        val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos)
        if (swipedPos < 0 || swipedViewHolder == null) return@OnTouchListener false

        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedItem = swipedViewHolder.itemView
        val rect = Rect()

        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(e) else {
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }
        false
    }

    init {
        buttons = ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        buttonsBuffer = HashMap()
        recoverQueue = object : LinkedList<Int>() {

            override fun add(element: Int) = if (contains(element)) false else super.add(element)
        }
        attachSwipe()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.absoluteAdapterPosition

        if (swipedPos != pos) {
            recoverQueue.add(swipedPos)
        }

        swipedPos = pos

        if (buttonsBuffer.containsKey(swipedPos)) {
            buttons = buttonsBuffer[swipedPos]!!
        } else buttons.clear()

        buttonsBuffer.clear()
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = viewHolder.itemView.width * .1f

    override fun getSwipeEscapeVelocity(defaultValue: Float) = .1f * defaultValue

    override fun getSwipeVelocityThreshold(defaultValue: Float) = 5f * defaultValue

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val pos = viewHolder.absoluteAdapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView

        if (pos < 0) {
            swipedPos = pos
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<UnderlayButton> = ArrayList()

                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(context, viewHolder, buffer)
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]!!
                }

                translationX = dX * buffer.size * BUTTON_WIDTH / itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }

        super.onChildDraw(
            c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive
        )
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (recoverQueue.isNotEmpty()) {
            recoverQueue.poll()?.let { pos ->
                if (pos > -1) {
                    recyclerView.adapter!!.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {

        val rightAbs = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size

        for (i in buffer.indices.reversed()) {
            val button = buffer[i]
            val right = if (i != 0) rightAbs - dButtonWidth else rightAbs
            val left = right - dButtonWidth

            if (!button.isDrawn && i == buffer.lastIndex) {
                touchListener.onTouch(recyclerView.adapter as TrackAdapter, button, pos)
                button.isDrawn = true
            }

            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )
        }
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    abstract fun instantiateUnderlayButton(
        context: Context,
        viewHolder: RecyclerView.ViewHolder,
        underlayButtons: MutableList<UnderlayButton>
    )

    class UnderlayButton(
        private val context: Context,
        var text: String,
        icon: Drawable?,
        private val bgColor: Int,
        private val textColor: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {

        var isDrawn = false
        private var pos = 0
        private var clickRegion: RectF? = null
        var image = drawableToBitmap(context, icon)

        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(pos)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rectF: RectF, pos: Int) {

            val r = Rect()
            val p = Paint()
            val cHeight = rectF.height()

            p.setColor(bgColor)
            c.drawRect(rectF, p)

            image?.let { img ->
                val iconHorizontalMargin = BUTTON_WIDTH / 2 - ICON_SIZE.dpToPx(context) / 2
                val iconVerticalMargin = cHeight / 2 - img.height / 2 - TEXT_SIZE / 1.5f

                c.drawBitmap(
                    img,
                    rectF.left + iconHorizontalMargin,
                    rectF.top + iconVerticalMargin,
                    p
                )
            }

            p.setColor(textColor)
            p.typeface = ResourcesCompat.getFont(context, R.font.ys_500)
            p.textSize = TEXT_SIZE
            p.isAntiAlias = true
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)

            val x = BUTTON_WIDTH / 2 - r.width() / 2
            val y = cHeight / 2 + r.height() / 2 - r.top + TEXT_SIZE

            c.drawText(text, rectF.left + x, rectF.top + y - 10, p)

            clickRegion = rectF
            this.pos = pos
        }
    }

    fun interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    fun interface TouchedTrackListener {
        fun onTouch(adapter: TrackAdapter, button: UnderlayButton, pos: Int)
    }
}