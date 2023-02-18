package com.eryuksa.catchthelines.data.dto

import com.google.gson.annotations.SerializedName

data class MediaContent(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("lineAudioUrls") val lineAudioUrls: List<String>
) {

    fun toGameItem(): GameItem =
        GameItem(id, title, posterUrl, lineAudioUrls)
}
