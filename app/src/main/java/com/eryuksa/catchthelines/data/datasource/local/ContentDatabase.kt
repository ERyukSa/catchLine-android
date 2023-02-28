package com.eryuksa.catchthelines.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eryuksa.catchthelines.data.dto.CaughtContent
import com.eryuksa.catchthelines.data.dto.Content
import com.eryuksa.catchthelines.data.dto.ContentDetail

@Database(entities = [Content::class, ContentDetail::class, CaughtContent::class], version = 4)
@TypeConverters(ContentTypeConverter::class)
abstract class ContentDatabase : RoomDatabase() {

    abstract fun contentDao(): ContentDAO

    companion object {
        private const val DATABASE_NAME = "content.db"

        private var INSTANCE: ContentDatabase? = null

        fun getInstance(context: Context): ContentDatabase {
            if (INSTANCE == null) {
                synchronized(ContentDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContentDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE ?: throw UninitializedPropertyAccessException()
        }
    }
}
