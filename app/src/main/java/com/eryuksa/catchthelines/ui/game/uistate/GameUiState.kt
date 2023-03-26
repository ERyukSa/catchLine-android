package com.eryuksa.catchthelines.ui.game.uistate

data class GameUiState(
    val currentPage: Int = 0,
    val contentItems: List<ContentItem> = emptyList(),
    val audioIndex: Int = 0,
    val usedHints: Set<Hint> = emptySet(),
    val firstCharacterHint: String = "",
    val characterCountHint: Int? = null,
    val resultText: String = "",
    val hintCount: Int = 10,
    val gameMode: GameMode = GameMode.WATCHING
)
