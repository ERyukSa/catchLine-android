package com.eryuksa.catchthelines.ui.game.uistate

data class GameItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val lineAudioUrls: List<String>,
    val feedbackUiState: FeedbackUiState,
    val usedHints: Set<Hint>
) {
    val blurDegree: Int
        get() {
            return when {
                feedbackUiState is UserCaughtTheLine -> 0
                usedHints.contains(Hint.CLEARER_POSTER) -> CLEARER_BLUR_DEGREE
                else -> DEFAULT_BLUR_DEGREE
            }
        }

    val isClickable: Boolean
        get() = feedbackUiState is UserCaughtTheLine

    companion object {
        private const val CLEARER_BLUR_DEGREE = 2
        private const val DEFAULT_BLUR_DEGREE = 6
    }
}
