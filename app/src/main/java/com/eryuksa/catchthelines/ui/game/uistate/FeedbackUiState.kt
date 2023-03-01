package com.eryuksa.catchthelines.ui.game.uistate

sealed interface FeedbackUiState {
    val gameCanContinue: Boolean
        get() = this !is UserCaughtTheLine
    val isPosterDraggable: Boolean
        get() = !gameCanContinue
}

class UserCaughtTheLine(val title: String) : FeedbackUiState
class UserInputWrong(val userInput: String) : FeedbackUiState
object NoInput : FeedbackUiState
object AllKilled : FeedbackUiState
