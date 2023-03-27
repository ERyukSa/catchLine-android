package com.eryuksa.catchthelines.ui.record

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eryuksa.catchthelines.data.dto.Content

@BindingAdapter("posterUrl")
fun ImageView.setPosterImage(posterUrl: String) {
    Glide.with(context)
        .load(posterUrl)
        .into(this)
}

@BindingAdapter("contents")
fun RecyclerView.setContents(contentList: List<Content>) {
    val adapter = this.adapter as? CaughtContentsAdapter ?: return
    adapter.contents = contentList
}
