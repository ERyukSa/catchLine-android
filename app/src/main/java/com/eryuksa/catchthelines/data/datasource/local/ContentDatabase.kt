package com.eryuksa.catchthelines.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail
import com.eryuksa.catchthelines.data.dto.EncounteredContent

@Database(entities = [Content::class, ContentDetail::class, EncounteredContent::class], version = 6)
@TypeConverters(ContentTypeConverter::class)
abstract class ContentDatabase : RoomDatabase() {

    abstract fun contentDao(): ContentDAO
}
