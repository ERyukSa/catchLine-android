package com.eryuksa.catchthelines.data.repository

import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.TriedContent
import com.eryuksa.catchthelines.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepository @Inject constructor(
    private val remoteDataSource: ContentRemoteDataSource,
    private val localDataSource: ContentLocalDataSource,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    private var uncaughtContentCount = 0
    private var randomIndices: List<Int> = emptyList()

    suspend fun getContents(offset: Int): List<Content> {
        return withContext(externalScope.coroutineContext) {
            if (offset == 0) {
                uncaughtContentCount = localDataSource.getUncaughtContentCount()
                randomIndices = MutableList(uncaughtContentCount) { i -> i }.apply { shuffle() }
            }

            if (offset < uncaughtContentCount) {
                return@withContext getContentsFromLocal(offset)
            }

            return@withContext remoteDataSource.getContents(CONTENT_FETCH_SIZE, offset).also {
                saveContentsInLocal(it)
            }
        }
    }

    private suspend fun getContentsFromLocal(offset: Int): List<Content> =
        localDataSource.getContents(
            randomIndices.subList(offset, offset + CONTENT_FETCH_SIZE)
        )

    private fun saveContentsInLocal(contents: List<Content>) {
        if (contents.isNotEmpty()) {
            externalScope.launch {
                localDataSource.saveContents(contents)
            }
        }
    }

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)

    suspend fun getCaughtContents(size: Int, offset: Int): List<Content> {
        return withContext(Dispatchers.IO) {
            localDataSource.getCaughtContents(size, offset)
        }
    }

    suspend fun saveCaughtContent(contentId: Int) {
        saveTriedContent(contentId, isCaught = true)
    }

    suspend fun saveTriedContent(contentId: Int) {
        saveTriedContent(contentId, isCaught = false)
    }

    private suspend fun saveTriedContent(contentId: Int, isCaught: Boolean) {
        externalScope.launch(Dispatchers.IO) {
            localDataSource.saveTriedContent(
                TriedContent(
                    id = contentId,
                    updatedTime = System.currentTimeMillis(),
                    isCaught = isCaught
                )
            )
        }
    }

    suspend fun getTriedContentsCount(): Int =
        localDataSource.getTriedContentsCount()

    suspend fun getCaughtContentsCount(): Int =
        localDataSource.getCaughtContentsCount()

    companion object {
        private const val CONTENT_FETCH_SIZE = 10
    }
}
