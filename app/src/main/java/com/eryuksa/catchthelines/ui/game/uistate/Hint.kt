package com.eryuksa.catchthelines.ui.game.uistate

import androidx.annotation.StringRes
import com.eryuksa.catchthelines.R

sealed interface Hint

object AnotherLineHint : Hint
object ClearerPosterHint : Hint
object FirstCharacterHint : Hint {
    @StringRes val stringResId = R.string.game_hint_text_first_character
}
object CharacterCountHint : Hint {
    @StringRes val stringResId = R.string.game_hint_text_characters_count
}
object NoHint : Hint {
    @StringRes val stringResId = R.string.game_listen_and_guess
}
