package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.EncounteredContent

@Dao
interface ContentDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insert(content: Content)

    @Insert(onConflict = REPLACE)
    suspend fun insert(contents: List<Content>)

    @Query(
        "SELECT content.* FROM content LEFT JOIN encountered_content " +
            "ON content.id = encountered_content.id AND encountered_content.isCaught = true " +
            "WHERE encountered_content.id IS null"
    )
    suspend fun getContents(): List<Content>

    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContent(id: Int): Content

    @Insert(onConflict = REPLACE)
    suspend fun insert(encounteredContent: EncounteredContent)

    @Query(
        "SELECT * FROM content INNER JOIN encountered_content " +
            "on content.id = encountered_content.id AND encountered_content.isCaught = true " +
            "ORDER BY encountered_content.updatedTime DESC LIMIT :limit OFFSET :offset"
    )
    suspend fun getCaughtContents(limit: Int, offset: Int): List<Content>

    @Query("SELECT COUNT(*) FROM encountered_content WHERE encountered_content.isCaught = TRUE")
    suspend fun getCaughtContentsCount(): Int

    @Query("SELECT COUNT(*) FROM encountered_content")
    suspend fun getEncounteredContentsCount(): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(contentDetail: ContentDetail)

    @Query("SELECT * FROM content_detail WHERE id = :id")
    suspend fun getContentDetail(id: Int): ContentDetail
}
