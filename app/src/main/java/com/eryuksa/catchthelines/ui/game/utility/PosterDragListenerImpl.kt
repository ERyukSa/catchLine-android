package com.eryuksa.catchthelines.ui.game.utility

import android.view.View
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.databinding.FragmentGameBinding
import com.eryuksa.catchthelines.ui.game.PosterDragListener

class PosterDragListenerImpl(
    private val binding: FragmentGameBinding,
    private val removeCaughtContent: () -> Unit
) : PosterDragListener {

    override fun onStartDrag() {
        binding.darkBackgroundCover.visibility = View.VISIBLE
        binding.ivContentRemovableArea.visibility = View.VISIBLE
    }

    override fun onDraggingPoster(y: Float) {
        when (isPosterInRemoveRange(y)) {
            true -> binding.ivContentRemovableArea.setBackgroundResource(
                R.drawable.game_white_filled_circle_button
            )
            false -> binding.ivContentRemovableArea.setBackgroundResource(
                R.drawable.game_outlined_circle_button
            )
        }
    }

    override fun isPosterRemovable(y: Float): Boolean =
        isPosterInRemoveRange(y)

    override fun onFinishDrag() {
        binding.darkBackgroundCover.visibility = View.GONE
        binding.ivContentRemovableArea.visibility = View.GONE
    }

    override fun onRemovePoster() {
        removeCaughtContent()
    }

    private fun isPosterInRemoveRange(y: Float): Boolean =
        binding.ivContentRemovableArea.y + binding.ivContentRemovableArea.height * 0.65 >=
            binding.viewPagerPoster.y + y
}
