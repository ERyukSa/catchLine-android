package com.eryuksa.catchthelines.data.dto

import com.eryuksa.catchthelines.ui.game.GameItem
import com.google.gson.annotations.SerializedName

data class Contents(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("lineAudioUrls") val lineAudioUrls: List<String>
) {

    fun toGameItem(): GameItem =
        GameItem(id, title, posterUrl, lineAudioUrls)
}
