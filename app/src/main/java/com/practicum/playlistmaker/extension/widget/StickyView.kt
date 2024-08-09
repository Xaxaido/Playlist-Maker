package com.practicum.playlistmaker.extension.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.extension.widget.recyclerView.SpaceItemDecoration

private const val ANIMATION_DURATION = 150L

class StickyView(
    private val recycler: RecyclerView,
    private val stickyView: View,
) {

    private var isStick = false

    fun reset() {
        isStick = false
        stickyView.apply {
            alpha = 0f
            translationY = 0f
        }
    }

    fun addRoom() {
        recycler.post {
            recycler.addItemDecoration(SpaceItemDecoration(stickyView.measuredHeight))
            update()
        }
    }

    fun update() {
        val parentRect = getVisibleRect(recycler)
        val btnRect = getVisibleRect(stickyView)
        val layoutManager = recycler.layoutManager as LinearLayoutManager
        val bottomVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        val view = layoutManager.findViewByPosition(bottomVisibleItemPosition)!!
        val viewRect = getVisibleRect(view)

        val distance = when {
            canStickToBottom(viewRect.bottom + stickyView.measuredHeight, parentRect.bottom) -> {
                isStick = true
                -btnRect.bottom + parentRect.bottom
            }
            !isStick -> {
                isStick = false
                view.bottom + stickyView.measuredHeight
            }
            else -> 0
        }

        if (distance != 0) animate(distance)
    }

    private fun canStickToBottom(viewPos: Int, parentPos: Int) = viewPos > parentPos && !isStick

    private fun getVisibleRect(view: View) = Rect().apply { view.getGlobalVisibleRect(this) }

    private fun animate(targetValue: Int) {
        val translationYAnim = ObjectAnimator.ofFloat(
            stickyView,
            "translationY",
            0f,
            targetValue.toFloat()
        ).apply {
            duration = ANIMATION_DURATION
        }

        val alphaAnim = ObjectAnimator.ofFloat(stickyView,
            "alpha",
            0f,
            1f
        ).apply {
            duration = ANIMATION_DURATION
            startDelay = 100
        }

        AnimatorSet().apply {
            playTogether(translationYAnim, alphaAnim)
            start()
        }
    }
}