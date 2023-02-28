package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentRepository(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource
) {

    suspend fun getMediaContents(): List<Content> =
        remoteDataSource.getMediaContents()

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)

    suspend fun saveCaughtContent(content: Content) {
        withContext(Dispatchers.IO) {
            localDataSource.saveContent(content.copy(isCaught = true))
        }
    }
}
