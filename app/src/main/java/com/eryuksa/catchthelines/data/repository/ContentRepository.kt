package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.EncounteredContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContentRepository(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource
) {

    suspend fun getContents(): List<Content> {
        return withContext(Dispatchers.IO) {
            val contentsFromLocal = localDataSource.getContents()
            if (contentsFromLocal.isNotEmpty()) {
                return@withContext contentsFromLocal
            }

            val contentsFromRemote = remoteDataSource.getContents()
            GlobalScope.launch {
                localDataSource.saveContents(contentsFromRemote)
            }
            return@withContext contentsFromRemote
        }
    }

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)

    suspend fun getCaughtContents(size: Int, offset: Int): List<Content> {
        return withContext(Dispatchers.IO) {
            localDataSource.getCaughtContents(size, offset)
        }
    }

    suspend fun saveCaughtContent(content: Content) {
        saveEncounteredContent(content, isCaught = true)
    }

    suspend fun saveEncounteredContent(content: Content) {
        saveEncounteredContent(content, isCaught = false)
    }

    private suspend fun saveEncounteredContent(content: Content, isCaught: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            localDataSource.saveEncounteredContent(
                EncounteredContent(
                    id = content.id,
                    updatedTime = System.currentTimeMillis(),
                    isCaught = isCaught
                )
            )
        }
    }

    suspend fun getEncounteredContentsCount(): Int =
        localDataSource.getEncounteredContentsCount()

    suspend fun getCaughtContentsCount(): Int =
        localDataSource.getCaughtContentsCount()
}
