package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail

@Dao
interface ContentDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insert(content: Content)

    @Query("SELECT * FROM content")
    suspend fun getContents(): List<Content>

    @Query("SELECT * FROM content WHERE isCaught = true")
    suspend fun getCaughtContents(): List<Content>

    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContent(id: Int): Content

    @Insert(onConflict = REPLACE)
    suspend fun insert(contentDetail: ContentDetail)

    @Query("SELECT * FROM content_detail")
    suspend fun getContentDetails(): List<ContentDetail>

    @Query("SELECT * FROM content_detail WHERE id = :id")
    suspend fun getContentDetail(id: Int): ContentDetail
}
