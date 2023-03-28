package com.eryuksa.catchthelines.ui.detail

import com.eryuksa.catchthelines.data.dto.ContentGenre

data class ContentDetailUiState(
    val title: String = "",
    val backdropPosterUrl: String = "",
    val mainPosterPathUrl: String = "",
    val genres: List<ContentGenre> = emptyList(),
    val overview: String = "",
    val shortSummary: String = "",
    val releaseDate: String = "",
    val runningTime: Int = 0,
    val audioUrls: List<String> = emptyList()
) {
    val genresAsString get() = genres.joinToString(", ") { it.name }
}
