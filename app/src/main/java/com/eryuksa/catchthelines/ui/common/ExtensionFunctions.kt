package com.eryuksa.catchthelines.ui.common

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide

fun ViewPager2.removeOverScroll() {
    if (this.childCount == 0) return
    this.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
}

fun Window.setStatusBarIconColor(isDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            if (isDark) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        decorView.systemUiVisibility = if (isDark) {
            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun Window.setLayoutVerticalLimit(hasLimit: Boolean) {
    if (hasLimit) {
        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    } else {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}

fun Context.getStatusBarHeight(): Int {
    val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (statusBarHeightId > 0) {
        resources.getDimensionPixelSize(statusBarHeightId)
    } else {
        0
    }
}

fun Context.getNavigationBarHeight(): Int {
    val navigationBarHeightId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (navigationBarHeightId > 0) {
        resources.getDimensionPixelSize(navigationBarHeightId)
    } else {
        0
    }
}

fun Glide.preloadImage(url: String) {
    Glide.with(context)
        .load(url)
        .preload()
}
