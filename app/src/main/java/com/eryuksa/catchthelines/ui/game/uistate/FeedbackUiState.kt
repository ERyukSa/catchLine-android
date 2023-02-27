package com.eryuksa.catchthelines.ui.game.uistate

sealed interface FeedbackUiState {
    val isUserInputEnabled: Boolean
        get() = this !is UserCaughtTheLine
}

class UserCaughtTheLine(val title: String) : FeedbackUiState
class UserInputWrong(val userInput: String) : FeedbackUiState
object NoInput : FeedbackUiState
object AllKilled : FeedbackUiState
