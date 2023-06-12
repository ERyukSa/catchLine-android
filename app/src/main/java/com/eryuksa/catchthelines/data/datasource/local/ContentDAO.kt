package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.TriedContent

@Dao
interface ContentDAO {

    @Insert(onConflict = REPLACE)
    suspend fun insert(content: Content)

    @Insert(onConflict = REPLACE)
    suspend fun insert(contents: List<Content>)

    @Query(
        "SELECT count(*) FROM content LEFT JOIN tried_content " +
            "ON content.id = tried_content.id AND tried_content.isCaught " +
            "WHERE tried_content.id IS null"
    )
    suspend fun getUncaughtContentCount(): Int

    @Query(
        "SELECT content.* FROM content LEFT JOIN tried_content " +
            "ON content.id = tried_content.id AND tried_content.isCaught " +
            "WHERE tried_content.id IS NULL AND content.`index` in (:indexList)"
    )
    suspend fun getContents(indexList: List<Int>): List<Content>

    @Query("SELECT * FROM content WHERE id = :id")
    suspend fun getContent(id: Int): Content

    @Insert(onConflict = REPLACE)
    suspend fun insert(triedContent: TriedContent)

    @Query(
        "SELECT * FROM content INNER JOIN tried_content " +
            "on content.id = tried_content.id AND tried_content.isCaught " +
            "ORDER BY tried_content.updatedTime DESC LIMIT :limit OFFSET :offset"
    )
    suspend fun getCaughtContents(limit: Int, offset: Int): List<Content>

    @Query("SELECT COUNT(*) FROM tried_content WHERE tried_content.isCaught")
    suspend fun getCaughtContentsCount(): Int

    @Query("SELECT COUNT(*) FROM tried_content")
    suspend fun getEncounteredContentsCount(): Int

    @Insert(onConflict = REPLACE)
    suspend fun insert(contentDetail: ContentDetail)

    @Query("SELECT * FROM content_detail WHERE id = :id")
    suspend fun getContentDetail(id: Int): ContentDetail
}
