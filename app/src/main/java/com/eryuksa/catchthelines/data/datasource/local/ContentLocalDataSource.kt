package com.eryuksa.catchthelines.data.datasource.local

import com.eryuksa.catchthelines.data.dto.Content

class ContentLocalDataSource(
    private val dao: ContentDAO
) {
    suspend fun saveContent(content: Content) {
        dao.insert(content)
    }
}
