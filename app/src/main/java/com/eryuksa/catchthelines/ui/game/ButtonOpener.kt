package com.eryuksa.catchthelines.ui.game

import android.animation.ObjectAnimator
import android.widget.Button
import androidx.core.view.doOnLayout
import com.eryuksa.catchthelines.R

class ButtonOpener(
    initialCeilHeight: Int,
    private val margin: Int,
    private val duration: Long
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
        }
    }

    fun openButtons() {
        innerButtons.forEach {
            it.open()
            it.button.elevation =
                it.button.resources.getDimension(R.dimen.game_hint_elevation_over_dark_cover)
        }
    }

    fun closeButtons() {
        innerButtons.forEach(InnerButton::close)
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
