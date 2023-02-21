package com.eryuksa.catchthelines.data.dto

import com.eryuksa.catchthelines.ui.game.uistate.FeedbackUiState

data class GameItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val lineAudioUrls: List<String>,
    val blurDegree: Int,
    val feedbackUiState: FeedbackUiState
)
