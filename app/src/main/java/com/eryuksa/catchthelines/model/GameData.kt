package com.eryuksa.catchthelines.model

import com.google.gson.annotations.SerializedName

data class GameData(
    @SerializedName("challenged_contents_number") val challengeNumber: Int,
    @SerializedName("caught_contents_number") val caughtNumber: Int,
    @SerializedName("contents") val contentList: List<Content>
)
