package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eryuksa.catchthelines.databinding.ItemPosterBinding
import com.eryuksa.catchthelines.ui.game.uistate.PosterInfo
import jp.wasabeef.glide.transformations.BlurTransformation

interface PosterDragHandler {

    fun onStartDrag()
    fun onDraggingPoster(y: Float)
    fun isPosterRemovable(y: Float): Boolean
    fun onFinishDrag(lastY: Float)
}

class PosterViewPagerAdapter(
    val dragListener: PosterDragHandler,
    val onClick: (position: Int) -> Unit
) :
    ListAdapter<PosterInfo, PosterViewPagerAdapter.PosterViewHolder>(diffUtil) {

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

        private var isDraggable = false
        private var initialX = 0f
        private var initialY = 0f
        private var lastTouchedRawX = 0f
        private var lastTouchedRawY = 0f
        private var lastY = 0f

        private val removeAnimator: ObjectAnimator by lazy {
            ObjectAnimator.ofFloat(binding.root, "translationY", -3000f)
                .setDuration(500).also {
                    it.doOnEnd {
                        dragListener.onFinishDrag(lastY)
                        rollBackToInitialPosition()
                    }
                }
        }

        init {
            binding.btnNavigateToDetail.setOnClickListener {
                onClick(layoutPosition)
            }
            binding.root.setOnTouchListener { _, event ->
                if (isDraggable) {
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
                    dragPoster(event.rawX - lastTouchedRawX, event.rawY - lastTouchedRawY)
                    saveLastTouchedRawPoint(event.rawX, event.rawY)
                    dragListener.onDraggingPoster(binding.root.y)
                }
                MotionEvent.ACTION_UP -> {
                    lastY = binding.root.y
                    if (dragListener.isPosterRemovable(lastY)) {
                        removeAnimator.start()
                        // removeAnimator.doOnEnd{}에서 else문 진행
                    } else {
                        dragListener.onFinishDrag(lastY)
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

        @SuppressLint("CheckResult")
        fun bind(posterInfo: PosterInfo) {
            initialX = binding.root.x
            initialY = binding.root.y

            posterInfo.canDrag.also { canDrag ->
                this.isDraggable = canDrag
                binding.root.isClickable = canDrag
                binding.btnNavigateToDetail.isVisible = canDrag
            }

            Glide.with(itemView.context)
                .load(posterInfo.posterUrl)
                .apply {
                    if (posterInfo.blurDegree > 0) {
                        this.apply(
                            RequestOptions.bitmapTransform(
                                BlurTransformation(25, posterInfo.blurDegree)
                            )
                        )
                    }
                }
                .into(binding.ivPoster)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PosterInfo>() {
            override fun areContentsTheSame(oldItem: PosterInfo, newItem: PosterInfo) =
                oldItem.blurDegree == newItem.blurDegree

            override fun areItemsTheSame(oldItem: PosterInfo, newItem: PosterInfo) =
                oldItem.id == newItem.id
        }
    }
}
