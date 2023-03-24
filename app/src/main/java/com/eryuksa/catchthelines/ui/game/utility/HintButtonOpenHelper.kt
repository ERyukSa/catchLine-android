package com.eryuksa.catchthelines.ui.game.utility

import android.animation.ObjectAnimator
import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter
import com.eryuksa.catchthelines.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

object HintButtonOpenHelper {

    fun createOpenAnimators(
        openerButton: FloatingActionButton,
        vararg hintButtons: Button
    ): List<ObjectAnimator> {
        return hintButtons.mapIndexed { i, hintButton ->
            val animatedDistance = calculateAnimatedDistance(openerButton, i)
            ObjectAnimator
                .ofFloat(hintButton, "TranslationY", -animatedDistance)
                .setDuration(400L)
        }
    }

    private fun calculateAnimatedDistance(openerButton: View, hintButtonIndex: Int): Float =
        (hintButtonIndex + 1) * (
            openerButton.height +
                openerButton.resources.getDimension(R.dimen.game_hintbutton_margin)
            )

    @JvmStatic
    @BindingAdapter("isHintOpen", "index", "hintOpenAnimatorList", requireAll = true)
    fun Button.openUp(isOpen: Boolean, index: Int, hintOpenAnimatorList: List<ObjectAnimator>?) {
        if (isOpen) {
            this.visibility = View.VISIBLE
            hintOpenAnimatorList?.getOrNull(index)?.start() ?: return
        } else {
            this.translationY = 0f
            this.visibility = View.GONE
        }
    }
}
