package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.databinding.ItemPosterBinding
import com.eryuksa.catchthelines.ui.game.uistate.PosterItem

interface PosterDragListener {

    fun onStartDrag()
    fun onDraggingPoster(y: Float)
    fun isPosterRemovable(y: Float): Boolean
    fun onFinishDrag()
    fun onRemovePoster()
}

class PosterViewPagerAdapter(
    val dragListener: PosterDragListener,
    val onClick: (position: Int) -> Unit
) : ListAdapter<PosterItem, PosterViewPagerAdapter.PosterViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.ivPoster.clipToOutline = true
        return PosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class PosterViewHolder(private val binding: ItemPosterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var canDrag = false
        private var initialX = 0f
        private var initialY = 0f
        private var lastTouchedRawX = 0f
        private var lastTouchedRawY = 0f

        private val removeAnimator: ObjectAnimator by lazy {
            ObjectAnimator.ofFloat(binding.root, "translationY", -3000f)
                .setDuration(500).also {
                    it.doOnEnd {
                        dragListener.onRemovePoster()
                        rollBackToInitialPosition()
                    }
                }
        }

        init {
            binding.btnNavigateToDetail.setOnClickListener {
                onClick(layoutPosition)
            }
            binding.root.setOnTouchListener { _, event ->
                if (canDrag) {
                    dragOnTouch(event)
                }
                true
            }
        }

        private fun dragOnTouch(event: MotionEvent) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dragListener.onStartDrag()
                    saveLastTouchedRawPoint(event.rawX, event.rawY)
                }
                MotionEvent.ACTION_MOVE -> {
                    dragListener.onDraggingPoster(binding.root.y)
                    dragPoster(event.rawX - lastTouchedRawX, event.rawY - lastTouchedRawY)
                    saveLastTouchedRawPoint(event.rawX, event.rawY)
                }
                MotionEvent.ACTION_UP -> {
                    dragListener.onFinishDrag()
                    if (dragListener.isPosterRemovable(binding.root.y)) {
                        removeAnimator.start()
                    } else {
                        rollBackToInitialPosition()
                    }
                }
            }
        }

        private fun saveLastTouchedRawPoint(rawX: Float, rawY: Float) {
            lastTouchedRawX = rawX
            lastTouchedRawY = rawY
        }

        private fun dragPoster(dx: Float, dy: Float) {
            binding.root.x += dx
            binding.root.y += dy
        }

        private fun rollBackToInitialPosition() {
            binding.root.x = initialX
            binding.root.y = initialY
        }

        fun bind(posterItem: PosterItem) {
            initialX = binding.root.x
            initialY = binding.root.y

            binding.posterItem = posterItem
            canDrag = posterItem.blurDegree == 0
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PosterItem>() {
            override fun areContentsTheSame(oldItem: PosterItem, newItem: PosterItem) =
                oldItem.blurDegree == newItem.blurDegree

            override fun areItemsTheSame(oldItem: PosterItem, newItem: PosterItem) =
                oldItem.id == newItem.id
        }
    }
}
