package com.eryuksa.catchthelines.ui.game.uistate

data class GameUiState(
    val currentPage: Int = 0,
    val contentUiStates: List<ContentUiState> = emptyList(),
    val usedHints: Set<Hint> = emptySet(),
    val hintText: String = "",
    val feedbackText: String = "",
    val didUserCatchTheLine: Boolean = false,
    val hintCount: Int = 10
) {
    val audioUris: List<List<String>>
        get() = contentUiStates.map { it.content.lineAudioUrls }
}
