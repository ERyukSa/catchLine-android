package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.MediaContent
import kotlinx.coroutines.flow.Flow

class ContentRepository(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource
) {

    suspend fun getMediaContents(): List<MediaContent> =
        remoteDataSource.getMediaContents()

    fun getAvailableHintCount(): Flow<Int> =
        localDataSource.getAvailableHintCount()

    suspend fun decreaseHintCount() =
        localDataSource.decreaseHintCount()

    suspend fun increaseHintCount() =
        localDataSource.increaseHintCount()

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)
}
