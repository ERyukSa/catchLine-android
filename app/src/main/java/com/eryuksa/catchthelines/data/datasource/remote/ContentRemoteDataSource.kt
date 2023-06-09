package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRemoteDataSource @Inject constructor(
    private val contentsApi: ContentsRetrofitApi,
    private val contentDetailApi: ContentDetailRetrofitApi
) {

    suspend fun getContents(limit: Int, offset: Int): List<Content> {
        val response = contentsApi.getMediaContents(limit, offset)
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
