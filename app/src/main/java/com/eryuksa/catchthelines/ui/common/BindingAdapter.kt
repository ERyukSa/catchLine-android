package com.eryuksa.catchthelines.ui.common

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("visible")
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("posterUrl")
fun ImageView.setPosterImage(posterUrl: String) {
    Glide.with(context)
        .load(posterUrl)
        .dontTransform()
        .into(this)
}

@BindingAdapter("clipToOutline")
fun View.bindClipToOutline(clipToOutline: Boolean) {
    this.clipToOutline = clipToOutline
}
