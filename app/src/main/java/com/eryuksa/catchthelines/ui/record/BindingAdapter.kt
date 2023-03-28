package com.eryuksa.catchthelines.ui.record

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eryuksa.catchthelines.data.dto.Content

@BindingAdapter("contents")
fun RecyclerView.setContents(contentList: List<Content>) {
    val adapter = this.adapter as? CaughtContentsAdapter ?: return
    adapter.contents = contentList
}
