package com.eryuksa.catchthelines.ui.record

import com.eryuksa.catchthelines.data.dto.Content

data class RecordUiState(
    val caughtContentsCount: Int = 0,
    val encounteredContentsCount: Int = 0,
    val caughtContents: List<Content> = emptyList()
)
