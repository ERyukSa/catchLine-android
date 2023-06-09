package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.TriedContent

@Database(entities = [Content::class, ContentDetail::class, TriedContent::class], version = 8)
@TypeConverters(ContentTypeConverter::class)
abstract class ContentDatabase : RoomDatabase() {

    abstract fun contentDao(): ContentDAO
}
