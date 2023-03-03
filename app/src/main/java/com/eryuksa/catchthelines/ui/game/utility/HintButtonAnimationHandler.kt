package com.eryuksa.catchthelines.ui.game.utility

import android.animation.ObjectAnimator
import android.view.View
import android.widget.Button
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.eryuksa.catchthelines.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HintButtonAnimationHandler(
    hintEntranceButton: FloatingActionButton,
    hintButtons: List<Button>,
    wasHintOpened: Boolean,
    private val darkBackgroundView: View
) {

    private val innerButtons = mutableListOf<InnerButton>()
    private var ceil = hintEntranceButton.height.toFloat()
    var isHintOpen = wasHintOpened
        private set

    init {
        hintEntranceButton.setOnClickListener {
            when (isHintOpen) {
                true -> closeHintAndDarkBackground()
                false -> openHintAndDarkBackground()
            }
        }

        hintEntranceButton.doOnLayout {
            ceil = it.height.toFloat()
            hintButtons.forEach(this::addInnerButton)
            rollbackToOpenState()
        }
    }

    private fun addInnerButton(button: Button) {
        button.doOnLayout {
            val openAnimator = ObjectAnimator.ofFloat(button, "TranslationY", -(ceil + MARGIN))
                .setDuration(ANIMATION_DURATION)
            val closeAnimator = ObjectAnimator.ofFloat(button, "TranslationY", 0f)
                .setDuration(ANIMATION_DURATION)
            innerButtons += InnerButton(button, openAnimator, closeAnimator)
            ceil += (button.height + MARGIN)
        }
    }

    private fun openHintAndDarkBackground() {
        openHint()
        isHintOpen = true
        darkBackgroundView.visibility = View.VISIBLE
    }

    fun closeHintAndDarkBackground() {
        closeHint()
        isHintOpen = false
        darkBackgroundView.visibility = View.GONE
    }

    private fun openHint() {
        innerButtons.forEach {
            it.button.isVisible = true
            it.button.elevation =
                it.button.resources.getDimension(R.dimen.game_hint_elevation_over_dark_cover)
            it.open()
        }
    }

    private fun closeHint() {
        innerButtons.forEach {
            it.close()
            it.button.isVisible = false
        }
    }

    private fun rollbackToOpenState() {
        if (isHintOpen) {
            openHintAndDarkBackground()
        }
    }

    private class InnerButton(
        val button: Button,
        private val openAnimator: ObjectAnimator,
        private val closeAnimator: ObjectAnimator
    ) {
        fun open() {
            openAnimator.start()
        }

        fun close() {
            closeAnimator.start()
        }
    }

    companion object {
        private const val MARGIN = 20
        private const val ANIMATION_DURATION = 400L
    }
}
