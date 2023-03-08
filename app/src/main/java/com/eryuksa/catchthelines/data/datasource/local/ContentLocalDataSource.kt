package com.eryuksa.catchthelines.data.datasource.local

import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.EncounteredContent

class ContentLocalDataSource(
    private val dao: ContentDAO
) {

    suspend fun getContents(): List<Content> =
        dao.getContents()

    suspend fun saveContents(contents: List<Content>) {
        dao.insert(contents)
    }

    suspend fun saveEncounteredContent(encounteredContent: EncounteredContent) {
        dao.insert(encounteredContent)
    }

    suspend fun getCaughtContents(limit: Int, offset: Int): List<Content> =
        dao.getCaughtContents(limit, offset)

    suspend fun getEncounteredContentsCount(): Int =
        dao.getEncounteredContentsCount()

    suspend fun getCaughtContentsCount(): Int =
        dao.getCaughtContentsCount()
}
