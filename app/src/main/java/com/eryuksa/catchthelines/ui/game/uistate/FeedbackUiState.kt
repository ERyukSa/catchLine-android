package com.eryuksa.catchthelines.ui.game.uistate

import androidx.annotation.StringRes

sealed class FeedbackUiState(
    @StringRes val stringResId: Int,
    val stringParam: String
)

class UserCaughtTheLine(@StringRes stringResId: Int, stringParam: String) :
    FeedbackUiState(stringResId, stringParam)
class UserInputWrong(@StringRes stringResId: Int, stringParam: String) :
    FeedbackUiState(stringResId, stringParam)
