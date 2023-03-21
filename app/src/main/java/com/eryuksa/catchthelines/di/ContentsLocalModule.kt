package com.eryuksa.catchthelines.di

import android.content.Context
import androidx.room.Room
import com.eryuksa.catchthelines.data.datasource.local.ContentDAO
import com.eryuksa.catchthelines.data.datasource.local.ContentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ContentsLocalModule {

    private const val DATABASE_NAME = "content.db"

    @Singleton
    @Provides
    fun provideContentLocalDatabase(@ApplicationContext context: Context): ContentDatabase =
        Room.databaseBuilder(
            context = context,
            klass = ContentDatabase::class.java,
            name = DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideContentDao(contentDatabase: ContentDatabase): ContentDAO =
        contentDatabase.contentDao()
}
