package com.eryuksa.catchthelines.ui.common

import android.animation.ObjectAnimator
import android.widget.Button
import androidx.core.view.doOnLayout

class ButtonOpener(initialCeilHeight: Int, private val margin: Int, private val duration: Long) {

    private var innerButtons: List<InnerButton> = emptyList()
    private var ceil = initialCeilHeight.toFloat()

    fun addInnerButton(button: Button) {
        button.doOnLayout {
            val openAnimator = ObjectAnimator.ofFloat(button, "TranslationY", -(ceil + margin))
                .setDuration(duration)
            val closeAnimator = ObjectAnimator.ofFloat(button, "TranslationY", 0f)
                .setDuration(duration)
            innerButtons += InnerButton(openAnimator, closeAnimator)
            ceil += (button.height + margin)
        }
    }

    fun openButtons() {
        innerButtons.forEach(InnerButton::open)
    }

    fun closeButtons() {
        innerButtons.forEach(InnerButton::close)
    }

    private class InnerButton(
        private val openAnimator: ObjectAnimator,
        private val closeAnimator: ObjectAnimator
    ) {
        fun open() =
            openAnimator.start()

        fun close() =
            closeAnimator.start()
    }
}
