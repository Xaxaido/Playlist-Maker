package com.practicum.playlistmaker.common.widgets.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.ui.TrackViewHolder
import java.util.LinkedList
import java.util.Queue

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    lateinit var buttons: MutableList<UnderlayButton>
    private lateinit var gestureDetector: GestureDetector
    private var swipedPos = -1
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>>
    private lateinit var recoverQueue: Queue<Int>
    private var isRecoveringSwipedItem = false

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
        if (swipedPos < 0 || swipedViewHolder == null || isRecoveringSwipedItem) {
            return@OnTouchListener false
        }

        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedItem = swipedViewHolder.itemView
        val rect = Rect()

        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) {
                gestureDetector.onTouchEvent(e)
            } else {
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }
        false
    }
    private val onItemTouchListener = object : RecyclerView.OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (isRecoveringSwipedItem) return true

            if (e.action == MotionEvent.ACTION_UP) {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    val clickedPosition = recyclerView.getChildAdapterPosition(childView)

                    if (swipedPos >= 0 && swipedPos != clickedPosition) {
                        isRecoveringSwipedItem = true
                        recoverQueue.add(swipedPos)
                        swipedPos = -1
                        recoverSwipedItem()
                        recyclerView.postDelayed({
                            isRecoveringSwipedItem = false
                        }, Util.ANIMATION_SHORT)
                        return true
                    }
                }
            }

            gestureDetector.onTouchEvent(e)
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    init {
        buttons = ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        recyclerView.setOnTouchListener(onTouchListener)
        recyclerView.addOnItemTouchListener(onItemTouchListener)
        buttonsBuffer = HashMap()
        recoverQueue = object : LinkedList<Int>() {

            override fun add(element: Int) = if (contains(element)) false else super.add(element)
        }

        attachSwipe()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ) = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is TrackViewHolder) {
            makeMovementFlags(0, ItemTouchHelper.START)
        } else 0
    }

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

    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long {
        if (animationType != ItemTouchHelper.ANIMATION_TYPE_DRAG) {
            return Util.ANIMATION_SHORT
        }

        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
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
        isCurrentlyActive: Boolean,
    ) {

        val pos = viewHolder.absoluteAdapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView

        if (pos < 0) {
            swipedPos = pos
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL
                val buffer: MutableList<UnderlayButton>

                if (!buttonsBuffer.containsKey(pos)) {
                    buffer = instantiateUnderlayButton()
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]!!
                }

                translationX = dX * buffer.size * Util.UNDERLAY_BUTTON_WIDTH / itemView.width
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
                    recyclerView.adapter?.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float,
    ) {

        val rightAbs = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size

        for (i in buffer.indices.reversed()) {
            val button = buffer[i]
            val right = if (i != 0) rightAbs - dButtonWidth else rightAbs
            val left = right - dButtonWidth

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

    fun attachSwipe(recycler: RecyclerView? = recyclerView) {
        ItemTouchHelper(this).apply {
            attachToRecyclerView(recycler)
        }
    }

    abstract fun instantiateUnderlayButton(): MutableList<UnderlayButton>
}