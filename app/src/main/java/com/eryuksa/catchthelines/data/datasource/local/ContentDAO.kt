package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.eryuksa.catchthelines.data.dto.CaughtContent
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail

@Dao
interface ContentDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insert(content: Content)

    @Insert(onConflict = REPLACE)
    suspend fun insert(contents: List<Content>)

    @Query("SELECT * FROM content")
    suspend fun getContents(): List<Content>

    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContent(id: Int): Content

    @Insert(onConflict = REPLACE)
    suspend fun insert(caughtContent: CaughtContent)

    @Query(
        "SELECT * FROM content INNER JOIN caught_content on content.id = caught_content.id " +
            "ORDER BY caught_content.updatedTime DESC"
    )
    suspend fun getCaughtContents(): List<Content>

    @Insert(onConflict = REPLACE)
    suspend fun insert(contentDetail: ContentDetail)

    @Query("SELECT * FROM content_detail")
    suspend fun getContentDetails(): List<ContentDetail>

    @Query("SELECT * FROM content_detail WHERE id = :id")
    suspend fun getContentDetail(id: Int): ContentDetail
}
