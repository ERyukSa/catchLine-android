package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.Content
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentsRetrofitApi {

    @GET("/game_items.json?orderBy=\"index\"&print=pretty")
    suspend fun getMediaContents(
        @Query("limitToFirst") limit: Int,
        @Query("startAt") offset: Int
    ): Response<Map<String, Content>>
}
