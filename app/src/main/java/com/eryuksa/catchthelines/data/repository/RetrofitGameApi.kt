package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.dto.GameItem
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitGameApi {

    @GET("/game_items.json")
    suspend fun getGameItems(): Response<Map<String, GameItem>>
}
