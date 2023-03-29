package com.eryuksa.catchthelines.ui.common

import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment

fun View.toSharedElementPair() =
    this to transitionName

fun Fragment.setSharedElementComebackTransition(targetView: View) {
    postponeEnterTransition()
    targetView.doOnPreDraw {
        startPostponedEnterTransition()
    }
}
