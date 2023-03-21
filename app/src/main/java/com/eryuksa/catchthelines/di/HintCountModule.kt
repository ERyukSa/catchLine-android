package com.eryuksa.catchthelines.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object HintCountModule {

    private const val DATA_STORE_NAME = "HINT_COUNT"
    private val Context.hintCountDataStore:
        DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    @Singleton
    @Provides
    fun provideHintCountDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.hintCountDataStore
}
