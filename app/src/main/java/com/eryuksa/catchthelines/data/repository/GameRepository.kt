package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.dto.Contents
import com.eryuksa.catchthelines.data.remote.Retrofit

class GameRepository {

    private val gameApi = Retrofit.getApi(RetrofitGameApi::class.java)

    suspend fun getGameItems(): List<Contents> {
        val response = gameApi.getGameItems()
        return if (response.isSuccessful) {
            response.body()?.values?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}
