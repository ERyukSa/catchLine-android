package com.eryuksa.catchthelines.data.datasource.local

import com.eryuksa.catchthelines.data.dto.CaughtContent
import com.eryuksa.catchthelines.data.dto.Content

class ContentLocalDataSource(
    private val dao: ContentDAO
) {

    suspend fun getContents(): List<Content> =
        dao.getContents()

    suspend fun saveContents(contents: List<Content>) {
        dao.insert(contents)
    }

    suspend fun saveCaughtContent(caughtContent: CaughtContent) {
        dao.insert(caughtContent)
    }

    suspend fun getCaughtContents(limit: Int, offset: Int): List<Content> =
        dao.getCaughtContents(limit, offset)
}
