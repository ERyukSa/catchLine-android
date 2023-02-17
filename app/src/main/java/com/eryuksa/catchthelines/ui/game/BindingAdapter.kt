package com.eryuksa.catchthelines.ui.game

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.eryuksa.catchline_android.R

@BindingAdapter("hintCount")
fun setHintCountText(textView: TextView, hintCount: Int) {
    textView.text = textView.context.getString(R.string.game_available_hint_count, hintCount)
}
