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
import com.eryuksa.catchthelines.ui.game.uistate.GameUiState
import com.eryuksa.catchthelines.ui.game.uistate.UserCaughtTheLine
import jp.wasabeef.glide.transformations.BlurTransformation

interface PosterEventListener {

    fun onClickPoster(contentId: Int)
    fun onStartDrag()
    fun onDraggingPoster(y: Float)
    fun isPosterRemovable(y: Float): Boolean
    fun onFinishDrag(lastY: Float)
}

class PosterViewPagerAdapter(private val eventListener: PosterEventListener) :
    ListAdapter<GameUiState, PosterViewPagerAdapter.PosterViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val binding = ItemPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.ivPoster.clipToOutline = true
        return PosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: PosterViewHolder) {
        super.onViewRecycled(holder)
        holder.rollBackToInitialPosition()
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class PosterViewHolder(private val binding: ItemPosterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isTouchable = false
        private var initialX = 0f
        private var initialY = 0f
        private var lastTouchedRawX = 0f
        private var lastTouchedRawY = 0f
        private var lastX = 0F
        private var lastY = 0f

        private val removeAnimator: ObjectAnimator by lazy {
            ObjectAnimator.ofFloat(binding.root, "translationY", -3000f).setDuration(500).also {
                it.doOnEnd { _ ->
                    eventListener.onFinishDrag(lastY)
                }
            }
        }

        init {
            binding.btnGoToDetail.setOnClickListener {
                eventListener.onClickPoster(getItem(layoutPosition).mediaContent.id)
            }
            binding.root.setOnTouchListener { rootView, event ->
                if (isTouchable.not()) {
                    return@setOnTouchListener true
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        eventListener.onStartDrag()
                        lastTouchedRawX = event.rawX
                        lastTouchedRawY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        rootView.x += event.rawX - lastTouchedRawX
                        rootView.y += event.rawY - lastTouchedRawY
                        eventListener.onDraggingPoster(rootView.y)
                        lastTouchedRawX = event.rawX
                        lastTouchedRawY = event.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        lastX = rootView.x
                        lastY = rootView.y
                        if (eventListener.isPosterRemovable(lastY)) {
                            removeAnimator.start()
                        } else {
                            eventListener.onFinishDrag(lastY)
                        }
                        rollBackToInitialPosition()
                    }
                }
                true
            }
        }

        @SuppressLint("CheckResult")
        fun bind(uiState: GameUiState) {
            initialX = binding.root.x
            initialY = binding.root.y

            (uiState.feedbackUiState is UserCaughtTheLine).also { isCaught ->
                binding.root.isClickable = isCaught
                binding.btnGoToDetail.isVisible = isCaught
                isTouchable = isCaught
            }

            Glide.with(itemView.context)
                .load(uiState.mediaContent.posterUrl)
                .apply {
                    if (uiState.blurDegree > 0) {
                        this.apply(
                            RequestOptions.bitmapTransform(
                                BlurTransformation(25, uiState.blurDegree)
                            )
                        )
                    }
                }
                .into(binding.ivPoster)
        }

        fun rollBackToInitialPosition() {
            binding.root.x = initialX
            binding.root.y = initialY
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GameUiState>() {
            override fun areContentsTheSame(oldState: GameUiState, newState: GameUiState) =
                oldState.blurDegree == newState.blurDegree &&
                    oldState.mediaContent.posterUrl == newState.mediaContent.posterUrl

            override fun areItemsTheSame(oldState: GameUiState, newState: GameUiState) =
                oldState.mediaContent.id == newState.mediaContent.id
        }
    }
}
