package com.eryuksa.catchthelines.ui.game.utility

import android.animation.ObjectAnimator
import android.view.View
import android.widget.Button
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.eryuksa.catchthelines.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HintButtonOpenHandler(
    hintEntranceButton: FloatingActionButton,
    hintButtons: List<Button>,
    wasHintOpened: Boolean,
    private val darkBackgroundView: View
) {

    private val innerButtons = mutableListOf<HintButton>()
    private var animationDistance = 0f
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
            animationDistance = it.height.toFloat() + hintEntranceButton.getHintButtonMargin()
            hintButtons.forEach(this::addInnerButton)
            rollbackToPrevState()
        }
    }

    private fun addInnerButton(button: Button) {
        button.doOnLayout {
            val openAnimator = ObjectAnimator
                .ofFloat(button, "TranslationY", -animationDistance)
                .setDuration(ANIMATION_DURATION)
            innerButtons += HintButton(button, openAnimator)
            animationDistance += (button.height + button.getHintButtonMargin())
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
            it.open()
        }
    }

    private fun closeHint() {
        innerButtons.forEach {
            it.close()
        }
    }

    private fun rollbackToPrevState() {
        if (isHintOpen) {
            openHintAndDarkBackground()
        }
    }

    private class HintButton(
        private val button: Button,
        private val openAnimator: ObjectAnimator
    ) {

        init {
            button.elevation = button.resources.getDimension(R.dimen.game_hintbutton_elevation)
            button.isVisible = false
        }

        fun open() {
            openAnimator.start()
            button.isVisible = true
        }

        fun close() {
            button.translationY = 0f
            button.isVisible = false
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 400L
    }
}

private fun <T : View> T.getHintButtonMargin() = resources.getDimension(R.dimen.game_hintbutton_margin)
