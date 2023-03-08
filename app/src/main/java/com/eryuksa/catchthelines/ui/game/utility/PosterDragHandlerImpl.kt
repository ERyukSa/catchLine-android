package com.eryuksa.catchthelines.ui.game.utility

import android.view.View
import androidx.core.view.isVisible
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.ui.game.PosterDragHandler

class PosterDragHandlerImpl(
    private val binding: FragmentGameBinding,
    private val removeCaughtContent: () -> Unit
) : PosterDragHandler {

    override fun onStartDrag() {
        binding.darkBackgroundCoverForPoster.visibility = View.VISIBLE
        binding.ivRemoveContent.isVisible = true
    }

    override fun onDraggingPoster(y: Float) {
        when (isContentInRemoveRange(y)) {
            true -> binding.ivRemoveContent.setBackgroundResource(R.drawable.game_white_filled_circle_button)
            false -> binding.ivRemoveContent.setBackgroundResource(R.drawable.game_outlined_circle_button)
        }
    }

    override fun isPosterRemovable(y: Float): Boolean =
        isContentInRemoveRange(y)

    override fun onFinishDrag(lastY: Float) {
        binding.darkBackgroundCoverForPoster.visibility = View.INVISIBLE
        binding.ivRemoveContent.isVisible = false
        if (isContentInRemoveRange(lastY)) {
            removeCaughtContent()
        }
    }

    private fun isContentInRemoveRange(y: Float): Boolean =
        binding.ivRemoveContent.y + binding.ivRemoveContent.height * 0.65 >=
            binding.viewPagerPoster.y + y
}
