package com.eryuksa.catchthelines.ui.game.uistate

import com.eryuksa.catchthelines.data.dto.Content

data class ContentUiState(
    val content: Content,
    val blurDegree: Int
) {
    val canDrag
        get() = blurDegree == 0
}
