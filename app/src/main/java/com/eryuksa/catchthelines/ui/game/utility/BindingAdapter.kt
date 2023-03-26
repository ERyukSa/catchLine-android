package com.eryuksa.catchthelines.ui.game.utility

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.eryuksa.catchthelines.R
import com.eryuksa.catchthelines.ui.game.uistate.GameMode

@BindingAdapter("resultText", "gameMode")
fun TextView.setResultText(resultText: String, gameMode: GameMode) {
    if (resultText.isBlank()) return
    text = when (gameMode) {
        GameMode.WATCHING -> null
        GameMode.IN_GAME -> resources.getString(R.string.game_result_wrong, resultText)
        GameMode.CATCH -> resources.getString(R.string.game_result_catch_the_line, resultText)
    }
}
