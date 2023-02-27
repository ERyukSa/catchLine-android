package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.MediaContent

class ContentRepository(
    private val remoteDataSource: ContentRemoteDataSource
) {

    suspend fun getMediaContents(): List<MediaContent> =
        remoteDataSource.getMediaContents()

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)
}
