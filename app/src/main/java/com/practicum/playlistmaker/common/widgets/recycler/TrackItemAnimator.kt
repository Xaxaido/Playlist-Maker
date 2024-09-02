package com.practicum.playlistmaker.common.widgets.recycler

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.practicum.playlistmaker.common.utils.Util

class TrackItemAnimator : SimpleItemAnimator() {

    private val interpolator = AccelerateDecelerateInterpolator()
    private val pendingRemovals = ArrayList<RecyclerView.ViewHolder>()
    private val pendingAdditions = ArrayList<RecyclerView.ViewHolder>()
    private val pendingMoves = ArrayList<MoveInfo>()
    private val pendingChanges = ArrayList<ChangeInfo>()

    private val runningRemovals = ArrayList<RecyclerView.ViewHolder>()
    private val runningAdditions = ArrayList<RecyclerView.ViewHolder>()
    private val runningMoves = ArrayList<MoveInfo>()
    private val runningChanges = ArrayList<ChangeInfo>()

    private data class MoveInfo(
        val holder: RecyclerView.ViewHolder,
        val fromX: Int, val fromY: Int,
        val toX: Int, val toY: Int
    )

    private data class ChangeInfo(
        var oldHolder: RecyclerView.ViewHolder?,
        var newHolder: RecyclerView.ViewHolder?
    )

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        endAnimation(holder)
        pendingRemovals.add(holder)
        return true
    }

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) = true

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView

        view.animate()
            .setDuration(Util.ANIMATION_SHORT)
            .alpha(0f)
            .translationY(view.height.toFloat())
            .scaleX(0.5f)
            .scaleY(0.5f)
            .setInterpolator(interpolator)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.alpha = 1f
                    view.translationY = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                    dispatchRemoveFinished(holder)
                    runningRemovals.remove(holder)
                    dispatchFinishedWhenDone()
                }

                override fun onAnimationCancel(animation: Animator) {
                    view.alpha = 1f
                    view.translationY = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
            }).start()

        runningRemovals.add(holder)
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        endAnimation(holder)
        val view = holder.itemView
        view.alpha = 0f
        view.translationY = view.height.toFloat()
        view.scaleX = 0.5f
        view.scaleY = 0.5f
        pendingAdditions.add(holder)
        return true
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        view.animate()
            .setDuration(300)
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(interpolator)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchAddFinished(holder)
                    runningAdditions.remove(holder)
                    dispatchFinishedWhenDone()
                }

                override fun onAnimationCancel(animation: Animator) {
                    view.alpha = 1f
                    view.translationY = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
            })
            .start()
        runningAdditions.add(holder)
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int,
    ): Boolean {
        endAnimation(holder)
        if (fromX == toX && fromY == toY) {
            dispatchMoveFinished(holder)
            return false
        }
        holder.itemView.translationX = (fromX - toX).toFloat()
        holder.itemView.translationY = (fromY - toY).toFloat()
        val moveInfo = MoveInfo(holder, fromX, fromY, toX, toY)
        pendingMoves.add(moveInfo)
        return true
    }

    private fun animateMoveImpl(moveInfo: MoveInfo) {
        val view = moveInfo.holder.itemView
        val deltaX = (moveInfo.toX - moveInfo.fromX).toFloat()
        val deltaY = (moveInfo.toY - moveInfo.fromY).toFloat()

        view.animate().cancel()

        view.translationX = deltaX
        view.translationY = deltaY
        view.scaleX = 1.0f
        view.scaleY = 1.0f

        view.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(interpolator)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    view.alpha = 0.7f
                }

                override fun onAnimationEnd(animation: Animator) {
                    view.alpha = 1f
                    dispatchMoveFinished(moveInfo.holder)
                    runningMoves.remove(moveInfo)
                    dispatchFinishedWhenDone()
                }

                override fun onAnimationCancel(animation: Animator) {
                    view.alpha = 1f
                }
            })
            .start()

        runningMoves.add(moveInfo)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        fromX: Int, fromY: Int, toX: Int, toY: Int,
    ): Boolean {
        endAnimation(oldHolder)
        endAnimation(newHolder)

        dispatchChangeFinished(oldHolder, true)
        dispatchChangeFinished(newHolder, false)
        return false
    }

    override fun runPendingAnimations() {
        val removalsPending = pendingRemovals.isNotEmpty()
        val additionsPending = pendingAdditions.isNotEmpty()
        val movesPending = pendingMoves.isNotEmpty()
        val changesPending = pendingChanges.isNotEmpty()

        if (!removalsPending && !additionsPending && !movesPending && !changesPending) {
            return
        }

        pendingRemovals.forEach { holder -> animateRemoveImpl(holder) }
        pendingRemovals.clear()

        pendingMoves.forEach { moveInfo ->
            animateMoveImpl(moveInfo)
        }
        pendingMoves.clear()

        pendingChanges.forEach { changeInfo ->
            dispatchChangeFinished(changeInfo.oldHolder, true)
            dispatchChangeFinished(changeInfo.newHolder, false)
        }
        pendingChanges.clear()

        pendingAdditions.forEach { holder -> animateAddImpl(holder) }
        pendingAdditions.clear()
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        if (pendingRemovals.remove(item)) {
            dispatchRemoveFinished(item)
        }
        if (pendingAdditions.remove(item)) {
            dispatchAddFinished(item)
        }
        pendingMoves.removeAll { moveInfo ->
            if (moveInfo.holder == item) {
                dispatchMoveFinished(item)
                true
            } else {
                false
            }
        }
        pendingChanges.removeAll { changeInfo ->
            when {
                changeInfo.oldHolder == item -> {
                    dispatchChangeFinished(item, true)
                    true
                }

                changeInfo.newHolder == item -> {
                    dispatchChangeFinished(item, false)
                    true
                }

                else -> false
            }
        }
        if (runningRemovals.remove(item)) {
            dispatchRemoveFinished(item)
        }
        if (runningAdditions.remove(item)) {
            dispatchAddFinished(item)
        }
        runningMoves.removeAll { moveInfo ->
            if (moveInfo.holder == item) {
                dispatchMoveFinished(item)
                true
            } else {
                false
            }
        }
        runningChanges.removeAll { changeInfo ->
            when {
                changeInfo.oldHolder == item -> {
                    dispatchChangeFinished(item, true)
                    true
                }

                changeInfo.newHolder == item -> {
                    dispatchChangeFinished(item, false)
                    true
                }

                else -> false
            }
        }
        dispatchFinishedWhenDone()
    }

    override fun endAnimations() {
        pendingRemovals.forEach {
            resetAnimation(it)
            dispatchRemoveFinished(it)
        }
        pendingRemovals.clear()

        pendingAdditions.forEach {
            resetAnimation(it)
            dispatchAddFinished(it)
        }
        pendingAdditions.clear()

        pendingMoves.forEach {
            resetAnimation(it.holder)
            dispatchMoveFinished(it.holder)
        }
        pendingMoves.clear()

        pendingChanges.forEach {
            it.oldHolder?.let { holder ->
                resetAnimation(holder)
                dispatchChangeFinished(holder, true)
            }
            it.newHolder?.let { holder ->
                resetAnimation(holder)
                dispatchChangeFinished(holder, false)
            }
        }
        pendingChanges.clear()

        if (!isRunning) return

        runningRemovals.forEach {
            resetAnimation(it)
            dispatchRemoveFinished(it)
        }
        runningRemovals.clear()
        runningAdditions.forEach {
            resetAnimation(it)
            dispatchAddFinished(it)
        }
        runningAdditions.clear()
        runningMoves.forEach {
            resetAnimation(it.holder)
            dispatchMoveFinished(it.holder)
        }
        runningMoves.clear()
        runningChanges.forEach {
            it.oldHolder?.let { holder ->
                resetAnimation(holder)
                dispatchChangeFinished(holder, true)
            }
            it.newHolder?.let { holder ->
                resetAnimation(holder)
                dispatchChangeFinished(holder, false)
            }
        }
        runningChanges.clear()
        dispatchFinishedWhenDone()
    }

    override fun isRunning(): Boolean {
        return pendingRemovals.isNotEmpty() || pendingAdditions.isNotEmpty() || pendingMoves.isNotEmpty() || pendingChanges.isNotEmpty() ||
                runningRemovals.isNotEmpty() || runningAdditions.isNotEmpty() || runningMoves.isNotEmpty() || runningChanges.isNotEmpty()
    }

    private fun resetAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply {
            alpha = 1f
            translationX = 0f
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
            animate().setInterpolator(null).setListener(null)
        }
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }
}