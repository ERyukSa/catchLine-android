package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.MediaContent

class GameRemoteDataSource(private val gameApi: RetrofitGameApi) {

    suspend fun getMediaContents(): List<MediaContent> {
        val response = gameApi.getMediaContents()
        return if (response.isSuccessful) {
            response.body()?.values?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }
}
