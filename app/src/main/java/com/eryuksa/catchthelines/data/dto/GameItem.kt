package com.eryuksa.catchthelines.data.dto

import com.google.gson.annotations.SerializedName

data class GameItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("lineAudioUrls") val lineAudioUrls: List<String>
)
