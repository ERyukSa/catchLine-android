package com.eryuksa.catchthelines.di

import com.eryuksa.catchthelines.BuildConfig
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofitApi
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
object ContentDetailNetworkModule {

    private const val CONTENT_DETAIL_BASE_URL = "https://api.themoviedb.org"
    private const val CONTENT_DETAIL_API_KEY = "312d719efb84a68ee202a8ce06eec62d"

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
            .addInterceptor { chain ->
                var request = chain.request()
                val url = request.url.newBuilder()
                    .addQueryParameter("api_key", CONTENT_DETAIL_API_KEY)
                    .addQueryParameter("language", "ko-KR").build()
                request = request.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideContentDetailApi(): ContentDetailRetrofitApi =
        Retrofit.Builder()
            .baseUrl(CONTENT_DETAIL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
            .create(ContentDetailRetrofitApi::class.java)
}
