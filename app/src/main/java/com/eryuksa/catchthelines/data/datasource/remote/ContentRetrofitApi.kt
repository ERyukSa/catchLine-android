package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.Content
import retrofit2.Response
import retrofit2.http.GET

interface ContentRetrofitApi {

    @GET("/game_items.json")
    suspend fun getMediaContents(): Response<Map<String, Content>>
}
