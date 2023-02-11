package com.eryuksa.catchthelines.model

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("audio_name") val audioName: String,  // 대사 오디오 이름
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("line_summary") val lineSummary: String, // 대사 한 줄 요약
    @SerializedName("showed") var challenged: Boolean,     // 봤었던 문제인지
    @SerializedName("caught") var caught: Boolean,    // 캐치한 문제인지
    @SerializedName("api_id") val apiId: Int // API 호출할 때 전달할 id
)
