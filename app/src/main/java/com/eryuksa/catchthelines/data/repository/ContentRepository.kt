package com.eryuksa.catchthelines.data.repository

import android.util.Log
import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.dto.CaughtContent
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
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
        }.also {
            Log.d("로그", "getContents: ${localDataSource.getContents()}")
        }
    }

    suspend fun getContentDetail(id: Int): ContentDetail? =
        remoteDataSource.getContentDetail(id)

    suspend fun saveCaughtContent(content: Content) {
        withContext(Dispatchers.IO) {
            localDataSource.saveCaughtContent(CaughtContent(content.id, System.currentTimeMillis()))

            val a = localDataSource.getCaughtContents()
            Log.d("로그", "caughtContents: $a")
        }
    }
}
