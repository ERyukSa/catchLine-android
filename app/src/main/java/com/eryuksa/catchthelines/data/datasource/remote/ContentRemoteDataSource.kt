package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail

class ContentRemoteDataSource(
    private val gameApi: ContentRetrofitApi,
    private val contentDetailApi: ContentDetailRetrofitApi
) {

    suspend fun getMediaContents(): List<Content> {
        val response = gameApi.getMediaContents()
        return if (response.isSuccessful) {
            response.body()?.values?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getContentDetail(id: Int): ContentDetail? {
        return contentDetailApi.getContentDetail(id).run {
            if (this.isSuccessful) {
                this.body()
            } else {
                null
            }
        }
    }
}
