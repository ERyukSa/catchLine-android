package com.eryuksa.catchthelines.ui.detail

import com.eryuksa.catchthelines.data.dto.ContentGenre

data class ContentDetailUiState(
    val title: String,
    val backdropPosterUrl: String,
    val mainPosterPathUrl: String,
    val genres: List<ContentGenre>,
    val overview: String,
    val shortSummary: String,
    val releaseDate: String,
    val runningTime: Int,
    val audioUrls: List<String>
)
