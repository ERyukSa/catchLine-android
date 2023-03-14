package com.eryuksa.catchthelines.ui.game

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.ui.game.uistate.Hint

@BindingAdapter("myHintType", "usedHints", requireAll = true)
fun setBackgroundResource(button: Button, myHintType: Hint, usedHints: Set<Hint>) {
    when (usedHints.contains(myHintType)) {
        true -> button.setBackgroundResource(R.drawable.game_white_filled_stroked_circle_button)
        false -> button.setBackgroundResource(R.drawable.game_white_filled_circle_button)
    }
    button.elevation = button.resources.getDimension(R.dimen.game_hintbutton_elevation)
}
