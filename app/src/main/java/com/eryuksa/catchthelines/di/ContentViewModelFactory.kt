package com.eryuksa.catchthelines.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eryuksa.catchthelines.data.datasource.local.ContentLocalDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofit
import com.eryuksa.catchthelines.data.datasource.remote.ContentDetailRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.ContentRemoteDataSource
import com.eryuksa.catchthelines.data.datasource.remote.ContentRetrofitApi
import com.eryuksa.catchthelines.data.datasource.remote.Retrofit
import com.eryuksa.catchthelines.data.repository.ContentRepository
import com.eryuksa.catchthelines.ui.common.StringProvider
import com.eryuksa.catchthelines.ui.detail.DetailViewModel
import com.eryuksa.catchthelines.ui.game.GameViewModel

class ContentViewModelFactory private constructor() :
    ViewModelProvider.Factory {

    private val gameApi = Retrofit.getApi(ContentRetrofitApi::class.java)
    private val contentDetailApi =
        ContentDetailRetrofit.getApi(ContentDetailRetrofitApi::class.java)
    private val contentRepository: ContentRepository by lazy {
        ContentRepository(
            ContentRemoteDataSource(gameApi, contentDetailApi),
            ContentLocalDataSource(application)
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(contentRepository, StringProvider.getInstance()) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(contentRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }

    companion object {

        private lateinit var application: Application
        private var instance: ContentViewModelFactory? = null

        fun initialize(_application: Application) {
            instance ?: synchronized(this) {
                instance ?: ContentViewModelFactory().also {
                    instance = it
                    application = _application
                }
            }
        }

        fun getInstance(): ContentViewModelFactory =
            instance ?: throw UninitializedPropertyAccessException()
    }
}
