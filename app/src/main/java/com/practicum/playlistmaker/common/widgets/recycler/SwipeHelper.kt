package com.practicum.playlistmaker.common.widgets.recycler

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.common.utils.Util
import com.practicum.playlistmaker.search.ui.recycler.TrackAdapter
import java.util.LinkedList
import java.util.Queue

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(
    private val recyclerView: RecyclerView,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var swipedPos = -1
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>> = mutableMapOf()
    private lateinit var recoverQueue: Queue<Int>
    private var isRecoveringSwipedItem = false
    private var isAnimationPlaying = false
    private val onItemTouchListener = object : RecyclerView.OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (isRecoveringSwipedItem || isAnimationPlaying) return true

            if (e.action == MotionEvent.ACTION_UP) {
                if (swipedPos < 0) return false

                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    val clickedPosition = recyclerView.getChildAdapterPosition(childView)

                    if (swipedPos >= 0 && swipedPos != clickedPosition) {
                        isRecoveringSwipedItem = true
                        recyclerView.postDelayed({
                            isRecoveringSwipedItem = false
                        }, Util.ANIMATION_SHORT)
                    }
                } else {
                    buttonsBuffer[swipedPos]?.forEach { it.handleClick(e) }
                }

                buttonsBuffer.clear()
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
                return true
            }

            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    init {
        recyclerView.addOnItemTouchListener(onItemTouchListener)
        recoverQueue = object : LinkedList<Int>() {

            override fun add(element: Int) = if (contains(element)) false else super.add(element)
        }

        attachSwipe()
    }

    fun disableClick() { isAnimationPlaying = true }
    fun enableClick() { isAnimationPlaying = false }

    fun startParticleAnimation(particleView: ParticleView, pos: Int, onAnimationEnd: () -> Unit) {
        val viewToRemove = recyclerView.findViewHolderForAdapterPosition(pos)?.itemView ?: return
        val bitmap = Bitmap.createBitmap(viewToRemove.width, viewToRemove.height, Bitmap.Config.ARGB_8888)

        Canvas(bitmap).also { viewToRemove.draw(it) }
        viewToRemove.isVisible = false
        particleView.animator = ParticleAnimator(
            recyclerView.context,
            particleView,
            bitmap,
            0f,
            viewToRemove.top.toFloat() + recyclerView.top
        )
        particleView.startAnimation { onAnimationEnd() }
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
        val position = viewHolder.absoluteAdapterPosition
        val item = (recyclerView.adapter as TrackAdapter).getItem(position)

        return if (item != null) {
            makeMovementFlags(0, ItemTouchHelper.START)
        } else 0
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.absoluteAdapterPosition

        if (swipedPos != pos) {
            recoverQueue.add(swipedPos)
        }

        swipedPos = pos
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
            if (dX < 0) {
                if (!buttonsBuffer.containsKey(pos)) {
                    buttonsBuffer[pos] = instantiateUnderlayButton(pos)
                }

                val buffer = buttonsBuffer[pos] ?: return
                if (buffer.isEmpty()) return
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

    private fun attachSwipe() {
        ItemTouchHelper(this).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    abstract fun instantiateUnderlayButton(pos: Int): MutableList<UnderlayButton>
}