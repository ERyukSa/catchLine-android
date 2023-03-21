package com.eryuksa.catchthelines.di

import com.eryuksa.catchthelines.BuildConfig
import com.eryuksa.catchthelines.data.datasource.remote.ContentsRetrofitApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ContentsNetworkModule {

    private const val CONTENTS_BASE_URL = "https://catchthelines-debc4-default-rtdb.firebaseio.com"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideContentsApi(): ContentsRetrofitApi =
        Retrofit.Builder()
            .baseUrl(CONTENTS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
            .create(ContentsRetrofitApi::class.java)
}
