package com.eryuksa.catchthelines.ui.game

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.ui.game.uistate.Hint

@BindingAdapter("hintCount")
fun setHintCountText(textView: TextView, hintCount: Int) {
    textView.text = textView.context.getString(R.string.game_available_hint_count, hintCount)
}

@BindingAdapter("myHintType", "usedHints", requireAll = true)
fun setBackgroundResource(button: Button, myHintType: Hint, usedHints: Set<Hint>?) {
    if (usedHints == null) return
    when (usedHints.contains(myHintType)) {
        true -> button.setBackgroundResource(R.drawable.game_white_filled_circle_button)
        false -> button.setBackgroundResource(R.drawable.game_white_filled_stroked_circle_button)
    }
}
