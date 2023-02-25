package com.eryuksa.catchthelines.ui.game.uistate

import com.eryuksa.catchthelines.data.dto.MediaContent

data class GameUiState(
    val mediaContent: MediaContent,
    val feedbackUiState: FeedbackUiState,
    val usedHints: Set<Hint>,
    val hintText: String
) {
    val blurDegree: Int
        get() {
            return when {
                feedbackUiState is UserCaughtTheLine -> 0
                usedHints.contains(ClearerPosterHint) -> CLEARER_BLUR_DEGREE
                else -> DEFAULT_BLUR_DEGREE
            }
        }

    companion object {
        private const val CLEARER_BLUR_DEGREE = 2
        private const val DEFAULT_BLUR_DEGREE = 6
    }
}
