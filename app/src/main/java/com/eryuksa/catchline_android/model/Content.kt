package com.eryuksa.catchline_android.model

data class Content(
    val id: Int,
    val title: String,
    val audioName: String,  // 대사 오디오 이름
    val posterUrl: String,
    val lineSummary: String, // 대사 한 줄 요약
    var challenged: Boolean,     // 봤었던 문제인지
    var caught: Boolean,    // 캐치한 문제인지
    val apiId: Int // API 호출할 때 전달할 id
)
