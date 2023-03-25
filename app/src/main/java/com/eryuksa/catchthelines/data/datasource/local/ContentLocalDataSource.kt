package com.eryuksa.catchthelines.data.datasource.local

import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.TriedContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentLocalDataSource @Inject constructor(
    private val dao: ContentDAO
) {

    suspend fun getContents(): List<Content> =
        dao.getContents()

    suspend fun saveContents(contents: List<Content>) {
        dao.insert(contents)
    }

    suspend fun saveTriedContent(triedContent: TriedContent) {
        dao.insert(triedContent)
    }

    suspend fun getCaughtContents(limit: Int, offset: Int): List<Content> =
        dao.getCaughtContents(limit, offset)

    suspend fun getTriedContentsCount(): Int =
        dao.getEncounteredContentsCount()

    suspend fun getCaughtContentsCount(): Int =
        dao.getCaughtContentsCount()
}
