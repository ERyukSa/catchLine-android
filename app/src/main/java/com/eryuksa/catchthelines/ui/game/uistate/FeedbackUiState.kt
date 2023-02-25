package com.eryuksa.catchthelines.ui.game.uistate

sealed interface FeedbackUiState

class UserCaughtTheLine(val title: String) : FeedbackUiState
class UserInputWrong(val userInput: String) : FeedbackUiState
object NoInput : FeedbackUiState
object AllKilled : FeedbackUiState
