package com.eryuksa.catchthelines.data.datasource.remote

import com.eryuksa.catchthelines.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ContentDetailRetrofit {

    private const val BASE_URL = "https://api.themoviedb.org"
    private const val API_KEY = "312d719efb84a68ee202a8ce06eec62d"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
    }

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
                val url =
                    request.url.newBuilder().addQueryParameter("api_key", API_KEY).addQueryParameter("language", "ko-KR").build()
                request = request.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()
    }

    fun<T> getApi(api: Class<T>): T = retrofit.create(api)
}
