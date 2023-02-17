package com.eryuksa.catchthelines.ui.game.model

data class GameItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val lineAudioUrls: List<String>,
    val blurDegree: Int = 6
)
