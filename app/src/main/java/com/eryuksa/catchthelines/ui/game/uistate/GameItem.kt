package com.eryuksa.catchthelines.ui.game.uistate

data class GameItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val lineAudioUrls: List<String>,
    val blurDegree: Int,
    val feedbackUiState: FeedbackUiState,
    val usedHints: Set<Hint>
)
