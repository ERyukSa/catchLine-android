package com.eryuksa.catchthelines.ui.game.uistate

data class ContentInfo(
    override val id: Int,
    val title: String,
    val audioUrls: List<String>,
    override val posterUrl: String,
    override val blurDegree: Int
) : PosterInfo(id, posterUrl, blurDegree)
