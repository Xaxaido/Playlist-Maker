package com.practicum.playlistmaker.common.widgets.recycler

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlin.math.max

class ItemAnimator: SimpleItemAnimator() {

    private val mPendingRemovals = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingAdditions = ArrayList<RecyclerView.ViewHolder>()
    private val mPendingMoves = ArrayList<MoveInfo>()
    private val mPendingChanges = ArrayList<ChangeInfo>()
    private var mAdditionsList: ArrayList<ArrayList<RecyclerView.ViewHolder>> = ArrayList()
    private var mMovesList: ArrayList<ArrayList<MoveInfo>> = ArrayList()
    private var mChangesList: ArrayList<ArrayList<ChangeInfo>> = ArrayList()
    private var mAddAnimations: ArrayList<RecyclerView.ViewHolder?> = ArrayList()
    private var mMoveAnimations: ArrayList<RecyclerView.ViewHolder?> = ArrayList()
    private var mRemoveAnimations: ArrayList<RecyclerView.ViewHolder?> = ArrayList()
    private var mChangeAnimations: ArrayList<RecyclerView.ViewHolder?> = ArrayList()
    private var sDefaultInterpolator: TimeInterpolator? = null

    data class MoveInfo(
        var holder: RecyclerView.ViewHolder,
        var fromX: Int,
        var fromY: Int,
        var toX: Int,
        var toY: Int,
    )

    data class ChangeInfo(
        var oldHolder: RecyclerView.ViewHolder?,
        var newHolder: RecyclerView.ViewHolder?,
        var fromX: Int = 0,
        var fromY: Int = 0,
        var toX: Int = 0,
        var toY: Int = 0,
    )

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()

        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return
        }
        // First, remove stuff
        for (holder: RecyclerView.ViewHolder in mPendingRemovals) {
            animateRemoveImpl(holder)
        }
        mPendingRemovals.clear()
        // Next, move stuff
        if (movesPending) {
            val moves = ArrayList<MoveInfo>()
            moves.addAll(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                for (moveInfo: MoveInfo in moves) {
                    animateMoveImpl(
                        moveInfo.holder, moveInfo.fromX, moveInfo.fromY,
                        moveInfo.toX, moveInfo.toY
                    )
                }
                moves.clear()
                mMovesList.remove(moves)
            }
            if (removalsPending) {
                val view = moves[0].holder.itemView
                view.postDelayed(mover, removeDuration)
            } else {
                mover.run()
            }
        }
        // Next, change stuff, to run in parallel with move animations
        if (changesPending) {
            val changes = ArrayList<ChangeInfo>()
            changes.addAll(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                for (change: ChangeInfo in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
                mChangesList.remove(changes)
            }
            if (removalsPending) {
                val holder = changes[0].oldHolder
                holder!!.itemView.postDelayed(changer, removeDuration)
            } else {
                changer.run()
            }
        }
        // Next, add stuff
        if (additionsPending) {
            val additions = ArrayList<RecyclerView.ViewHolder>()
            additions.addAll(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                for (holder: RecyclerView.ViewHolder in additions) {
                    animateAddImpl(holder)
                }
                additions.clear()
                mAdditionsList.remove(additions)
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration = if (removalsPending) removeDuration else 0
                val moveDuration = if (movesPending) moveDuration else 0
                val changeDuration = if (changesPending) changeDuration else 0
                val totalDelay =
                    (removeDuration + max(
                        moveDuration.toDouble(),
                        changeDuration.toDouble()
                    )).toLong()
                val view = additions[0].itemView
                view.postDelayed(adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        holder.itemView.apply {
            alpha = 0f
        }
        mPendingRemovals.add(holder)

        return true
    }

    private fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()

        mRemoveAnimations.add(holder)
        animation.setDuration(removeDuration)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animator: Animator) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    view.alpha = 1f
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        resetAnimation(holder)
        holder.itemView.apply {
            alpha = 0f
            translationY = height.toFloat()
            scaleX = 0.5f
            scaleY = 0.5f
        }
        mPendingAdditions.add(holder)

        return true
    }

    private fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()

        mAddAnimations.add(holder)
        animation.alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(addDuration)
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animator: Animator) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationCancel(animator: Animator) {
                    view.alpha = 1f
                    view.translationY = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }

                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int,
        toX: Int, toY: Int
    ): Boolean {
        var x = fromX
        var y = fromY
        val view = holder.itemView

        x += view.translationX.toInt()
        y += view.translationY.toInt()
        resetAnimation(holder)

        val deltaX = toX - x
        val deltaY = toY - y
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            view.translationX = -deltaX.toFloat()
        }
        if (deltaY != 0) {
            view.translationY = -deltaY.toFloat()
        }
        mPendingMoves.add(MoveInfo(holder, x, y, toX, toY))

        return true
    }

    private fun animateMoveImpl(
        holder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }

        val animation = view.animate()
        mMoveAnimations.add(holder)
        animation.setDuration(moveDuration)
            .setListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animator: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(animator: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int
    ): Boolean {
        if (oldHolder === newHolder) {
            return animateMove(oldHolder, fromLeft, fromTop, toLeft, toTop)
        }
        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha
        resetAnimation(oldHolder)
        val deltaX = (toLeft - fromLeft - prevTranslationX).toInt()
        val deltaY = (toTop - fromTop - prevTranslationY).toInt()

        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha

        resetAnimation(newHolder)
        newHolder.itemView.translationX = -deltaX.toFloat()
        newHolder.itemView.translationY = -deltaY.toFloat()
        newHolder.itemView.alpha = 0f
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop))

        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val holder = changeInfo.oldHolder
        val view = holder?.itemView
        val newHolder = changeInfo.newHolder
        val newView = newHolder?.itemView

        if (view != null) {
            val oldViewAnim = view.animate().setDuration(
                changeDuration
            )
            mChangeAnimations.add(changeInfo.oldHolder)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f)
                .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animator: Animator) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(animator: Animator) {
                    oldViewAnim.setListener(null)
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(changeInfo.oldHolder)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
        if (newView != null) {
            val newViewAnimation = newView.animate()
            mChangeAnimations.add(changeInfo.newHolder)
            newViewAnimation.translationX(0f)
                .translationY(0f)
                .setDuration(changeDuration)
                .alpha(1f)
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(changeInfo.newHolder, false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        newView.alpha = 1f
                        newView.translationX = 0f
                        newView.translationY = 0f
                        dispatchChangeFinished(changeInfo.newHolder, false)
                        mChangeAnimations.remove(changeInfo.newHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(
        infoList: MutableList<ChangeInfo>,
        item: RecyclerView.ViewHolder
    ) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder)
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder)
        }
    }

    private fun endChangeAnimationIfNecessary(
        changeInfo: ChangeInfo,
        item: RecyclerView.ViewHolder?
    ): Boolean {
        var oldItem = false

        if (changeInfo.newHolder === item) {
            changeInfo.newHolder = null
        } else if (changeInfo.oldHolder === item) {
            changeInfo.oldHolder = null
            oldItem = true
        } else {
            return false
        }

        item!!.itemView.apply {
            alpha = 1f
            translationX = 0f
            translationY = 0f
        }
        dispatchChangeFinished(item, oldItem)

        return true
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        val view = item.itemView
        view.animate().cancel()
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            view.alpha = 1f
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            view.alpha = 1f
            dispatchAddFinished(item)
        }
        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                view.alpha = 1f
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }
        dispatchFinishedWhenDone()
    }

    private fun resetAnimation(holder: RecyclerView.ViewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = AccelerateDecelerateInterpolator()
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator)
        endAnimation(holder)
    }

    override fun isRunning() = mPendingAdditions.isNotEmpty()
        || mPendingChanges.isNotEmpty()
        || mPendingMoves.isNotEmpty()
        || mPendingRemovals.isNotEmpty()
        || mMoveAnimations.isNotEmpty()
        || mRemoveAnimations.isNotEmpty()
        || mAddAnimations.isNotEmpty()
        || mChangeAnimations.isNotEmpty()
        || mMovesList.isNotEmpty()
        || mAdditionsList.isNotEmpty()
        || mChangesList.isNotEmpty()

    fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    override fun endAnimations() {
        var count = mPendingMoves.size

        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            val view = item.holder.itemView
            view.translationY = 0f
            view.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }

        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }

        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            item.itemView.alpha = 1f
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }

        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning) {
            return
        }

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }

        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                additions.removeAt(j)
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }

        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)
        dispatchAnimationsFinished()
    }

    private  fun cancelAll(viewHolders: List<RecyclerView.ViewHolder?>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i]!!.itemView.animate().cancel()
        }
    }

    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) = payloads.isNotEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads)
}