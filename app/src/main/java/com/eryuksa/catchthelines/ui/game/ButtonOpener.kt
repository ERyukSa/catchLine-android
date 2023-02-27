package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.widget.Button
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.eryuksa.catchthelines.R

class ButtonOpener(
    initialCeilHeight: Int,
    private val margin: Int,
    private val duration: Long,
    var isOpen: Boolean = false
) {

    private val innerButtons = mutableListOf<InnerButton>()
    private var ceil = initialCeilHeight.toFloat()

    fun addInnerButton(button: Button) {
        button.doOnLayout {
            val openAnimator = ObjectAnimator.ofFloat(button, "TranslationY", -(ceil + margin))
                .setDuration(duration)
            val closeAnimator = ObjectAnimator.ofFloat(button, "TranslationY", 0f)
                .setDuration(duration)
            innerButtons += InnerButton(button, openAnimator, closeAnimator)
            ceil += (button.height + margin)
            if (isOpen) openAnimator.start() else closeAnimator.start()
        }
    }

    fun switchOpenState() {
        when (isOpen) {
            true -> openButtons()
            false -> closeButtons()
        }
        isOpen = !isOpen
    }

    private fun openButtons() {
        innerButtons.forEach {
            it.button.isVisible = true
            it.button.elevation =
                it.button.resources.getDimension(R.dimen.game_hint_elevation_over_dark_cover)
            it.open()
        }
    }

    private fun closeButtons() {
        innerButtons.forEach {
            it.close()
            it.button.isVisible = false
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
}
