package com.eryuksa.catchthelines.ui.record

import com.eryuksa.catchthelines.data.dto.Content

data class RecordUiState(
    val caughtContentsCount: Int,
    val tryContentsCount: Int,
    val caughtContents: List<Content>
)
