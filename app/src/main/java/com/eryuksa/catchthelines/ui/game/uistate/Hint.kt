package com.eryuksa.catchthelines.ui.game.uistate

sealed interface Hint {

    object ClearerPoster : Hint
    object FirstCharacter : Hint
    object CharacterCount : Hint
}

