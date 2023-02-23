package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.ContentDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ContentDetailRetrofitApi {

    @GET("/3/movie/{id}")
    suspend fun getContentDetail(@Path("id") id: Int): Response<ContentDetail>
}
