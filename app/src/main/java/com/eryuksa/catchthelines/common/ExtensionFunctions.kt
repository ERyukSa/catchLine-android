package com.eryuksa.catchthelines.common

import android.view.View
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.removeOverScroll() {
    if (this.childCount == 0) return
    this.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
}